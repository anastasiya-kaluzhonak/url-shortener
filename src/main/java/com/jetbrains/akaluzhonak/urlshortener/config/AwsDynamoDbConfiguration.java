package com.jetbrains.akaluzhonak.urlshortener.config;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
public class AwsDynamoDbConfiguration {

    @Bean
    public DynamoDbClient dynamoDbClient(
            @Value("${com.jetbrains.akaluzhonak.urlshortener.dynamodb.region:ue-central-1}") final String region,
            @Value("${com.jetbrains.akaluzhonak.urlshortener.dynamodb.endpointOverride}") final String endpointOverride
    ) {
        final DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(region));
        if (!StringUtils.isBlank(endpointOverride)) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }
}
