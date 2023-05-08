package com.jetbrains.akaluzhonak.urlshortener.dao;

import com.jetbrains.akaluzhonak.urlshortener.exception.InternalServerException;
import com.jetbrains.akaluzhonak.urlshortener.exception.NotFoundException;
import com.jetbrains.akaluzhonak.urlshortener.model.dao.Link;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DynamoDbShortenedLinkDao implements ShortenedLinkDao {

    private static final String ID_ATTRIBUTE = "Id";
    private static final String ORIGINAL_LINK_ATTRIBUTE = "Origin";
    private static final String DESCRIPTION_ATTRIBUTE = "Description";
    private static final String CREATED_AT_ATTRIBUTE = "CreatedAt";
    private static final String EXPIRE_AT_ATTRIBUTE = "ExpireAt";
    private static final List<String> RETRIEVED_LINK_ATTRIBUTES = List.of(ID_ATTRIBUTE, ORIGINAL_LINK_ATTRIBUTE, DESCRIPTION_ATTRIBUTE, CREATED_AT_ATTRIBUTE, EXPIRE_AT_ATTRIBUTE);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbShortenedLinkDao(
            final DynamoDbClient dynamoDbClient,
            @Value("${com.jetbrains.akaluzhonak.urlshortener.dynamodb.table:links}") final String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public List<Link> getAll() {
        final ScanResponse response = dynamoDbClient.scan(ScanRequest.builder()
                .tableName(tableName)
                .limit(10)
                .attributesToGet(RETRIEVED_LINK_ATTRIBUTES)
                .build());

        return response.items().stream().map(this::attributesToLink).collect(Collectors.toList());
    }

    @Override
    public Link get(final String id) {
        final GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(ID_ATTRIBUTE, AttributeValue.fromS(id)))
                .attributesToGet(RETRIEVED_LINK_ATTRIBUTES)
                .build());

        if (!response.hasItem()) {
            final String message = String.format("The shortened link with id = %s is not found", id);
            log.error(message);
            throw new NotFoundException(message);
        }

        return attributesToLink(response.item());
    }

    @Override
    public void save(final Link link) {
        try {
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(linkToAttributes(link))
                    .conditionExpression("attribute_not_exists(Id)")
                    .build());
        } catch (ConditionalCheckFailedException e) {
            final String message = String.format("The shortened link with id = %s already exists", link.getId());
            log.error(message);
            throw new InternalServerException(message, e);
        }
    }

    @Override
    public Link updateDescription(final String id, final String description) {
        try {
            UpdateItemRequest.Builder requestBuilder = UpdateItemRequest.builder()
                    .tableName(tableName)
                    .conditionExpression("attribute_exists(Id)")
                    .key(Map.of(ID_ATTRIBUTE, AttributeValue.fromS(id)))
                    .returnValues(ReturnValue.ALL_NEW);
            requestBuilder = StringUtils.isNotBlank(description)
                    ? requestBuilder
                    .updateExpression("SET Description = :desc")
                    .expressionAttributeValues(Map.of(":desc", AttributeValue.fromS(description)))
                    : requestBuilder.updateExpression("REMOVE Description");

            UpdateItemResponse response = dynamoDbClient.updateItem(requestBuilder.build());

            return attributesToLink(response.attributes());
        } catch (ConditionalCheckFailedException e) {
            final String message = String.format("The shortened link with id = %s is not found", id);
            log.error(message);
            throw new NotFoundException(message, e);
        }
    }

    @Override
    public void delete(final String id) {
        try {
            dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(ID_ATTRIBUTE, AttributeValue.fromS(id)))
                    .conditionExpression("attribute_exists(Id)")
                    .build());
        } catch (ConditionalCheckFailedException e) {
            final String message = String.format("The shortened link with id = %s is not found", id);
            log.error(message);
            throw new NotFoundException(message, e);
        }
    }

    private Link attributesToLink(final Map<String, AttributeValue> attributes) {
        final String id = attributes.get(ID_ATTRIBUTE).s();
        final String originalLink = attributes.get(ORIGINAL_LINK_ATTRIBUTE).s();
        final String description = attributes.get(DESCRIPTION_ATTRIBUTE) != null
                ? attributes.get(DESCRIPTION_ATTRIBUTE).s()
                : null;
        final LocalDateTime createdAt = LocalDateTime.parse(attributes.get(CREATED_AT_ATTRIBUTE).s());
        long expireAtInSeconds = Long.getLong(attributes.get(EXPIRE_AT_ATTRIBUTE).n());

        return Link.builder()
                .id(id)
                .originalLink(originalLink)
                .description(description)
                .createdAt(createdAt)
                .expireAtInSeconds(expireAtInSeconds)
                .build();
    }

    private Map<String, AttributeValue> linkToAttributes(final Link link) {
        return Map.of(
                ID_ATTRIBUTE, AttributeValue.fromS(link.getId()),
                ORIGINAL_LINK_ATTRIBUTE, AttributeValue.fromS(link.getOriginalLink()),
                DESCRIPTION_ATTRIBUTE, AttributeValue.fromS(link.getDescription()),
                CREATED_AT_ATTRIBUTE, AttributeValue.fromS(link.getCreatedAt().toString()),
                EXPIRE_AT_ATTRIBUTE, AttributeValue.fromN(String.valueOf(link.getExpireAtInSeconds()))
        );
    }
}
