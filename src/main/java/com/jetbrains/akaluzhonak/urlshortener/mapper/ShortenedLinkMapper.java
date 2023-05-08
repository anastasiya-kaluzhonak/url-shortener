package com.jetbrains.akaluzhonak.urlshortener.mapper;

import com.jetbrains.akaluzhonak.urlshortener.model.dao.Link;
import com.jetbrains.akaluzhonak.urlshortener.model.dto.LinkDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.zip.CRC32;

@Component
public class ShortenedLinkMapper {

    private final Clock clock;
    private final int validPeriodInMonths;

    public ShortenedLinkMapper(final Clock clock,
                               @Value("${com.jetbrains.akaluzhonak.urlshortener.dynamodb.validPeriodInMonths:1}") final int validPeriodInMonths
    ) {
        this.clock = clock;
        this.validPeriodInMonths = validPeriodInMonths;
    }

    public LinkDto mapToDto(final Link link) {
        return LinkDto.builder()
                .id(link.getId())
                .originalLink(link.getOriginalLink())
                .description(link.getDescription())
                .createdAt(link.getCreatedAt())
                .build();
    }

    public Link map(final LinkDto linkDto) {

        final CRC32 hash = new CRC32();
        hash.update(linkDto.getOriginalLink().getBytes());
        final String id = Long.toHexString(hash.getValue());

        final LocalDateTime createdAt = LocalDateTime.now(clock);
        final long expireAtInSeconds = createdAt.plusMonths(validPeriodInMonths).toEpochSecond(ZoneOffset.UTC);

        return Link.builder()
                .id(id)
                .originalLink(linkDto.getOriginalLink())
                .description(linkDto.getDescription())
                .createdAt(createdAt)
                .expireAtInSeconds(expireAtInSeconds)
                .build();
    }
}
