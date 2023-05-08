package com.jetbrains.akaluzhonak.urlshortener.controller;

import com.jetbrains.akaluzhonak.urlshortener.service.ShortenedLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectShortenedUrlController {

    private final ShortenedLinkService shortenedUrlService;

    public RedirectShortenedUrlController(final ShortenedLinkService shortenedUrlService) {
        this.shortenedUrlService = shortenedUrlService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> redirect(@PathVariable("id") final String id) {
        final String originalUrl = shortenedUrlService.getOriginalLink(id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
