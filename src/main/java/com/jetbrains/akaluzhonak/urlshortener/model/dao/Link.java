package com.jetbrains.akaluzhonak.urlshortener.model.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Link {
    private String id;
    private String originalLink;
    private String description;
    private LocalDateTime createdAt;
    private long expireAtInSeconds;
}
