package com.jetbrains.akaluzhonak.urlshortener.controller;

import com.jetbrains.akaluzhonak.urlshortener.model.dto.LinkDto;
import com.jetbrains.akaluzhonak.urlshortener.model.dto.UpdatedLinkDto;
import com.jetbrains.akaluzhonak.urlshortener.service.ShortenedLinkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/links")
public class ShortenedUrlController {

    private final ShortenedLinkService shortenedUrlService;

    public ShortenedUrlController(final ShortenedLinkService shortenedUrlService) {
        this.shortenedUrlService = shortenedUrlService;
    }

    @GetMapping
    public List<LinkDto> getAllShortenedLinks() {
        return shortenedUrlService.getAllShortenedLinks();
    }

    @GetMapping("/{id}")
    public LinkDto getShortenedLink(@PathVariable final String id) {
        return shortenedUrlService.getShortenedLink(id);
    }

    @PostMapping
    public LinkDto createShortenedLink(@RequestBody @Valid final LinkDto link) {
        return shortenedUrlService.createShortenedLink(link);
    }

    @PatchMapping("/{id}")
    public LinkDto updateShortenedLink(@PathVariable final String id, @RequestBody @Valid final UpdatedLinkDto link) {
        return shortenedUrlService.updateShortenedLink(id, link);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShortenedLink(@PathVariable final String id) {
        shortenedUrlService.deleteShortenedLink(id);
    }
}
