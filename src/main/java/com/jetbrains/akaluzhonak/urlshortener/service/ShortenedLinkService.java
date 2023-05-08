package com.jetbrains.akaluzhonak.urlshortener.service;

import com.jetbrains.akaluzhonak.urlshortener.dao.ShortenedLinkDao;
import com.jetbrains.akaluzhonak.urlshortener.exception.LinkAlreadyExpiredException;
import com.jetbrains.akaluzhonak.urlshortener.mapper.ShortenedLinkMapper;
import com.jetbrains.akaluzhonak.urlshortener.model.dao.Link;
import com.jetbrains.akaluzhonak.urlshortener.model.dto.LinkDto;
import com.jetbrains.akaluzhonak.urlshortener.model.dto.UpdatedLinkDto;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShortenedLinkService {

    private final ShortenedLinkDao shortenedLinkDao;
    private final ShortenedLinkMapper shortenedLinkMapper;
    private final Clock clock;

    public ShortenedLinkService(final ShortenedLinkDao shortenedLinkDao,
                                final ShortenedLinkMapper shortenedLinkMapper,
                                final Clock clock
    ) {
        this.shortenedLinkDao = shortenedLinkDao;
        this.shortenedLinkMapper = shortenedLinkMapper;
        this.clock = clock;
    }

    public List<LinkDto> getAllShortenedLinks() {
        return shortenedLinkDao.getAll()
                .stream()
                .map(shortenedLinkMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public LinkDto getShortenedLink(final String id) {
        final Link link = shortenedLinkDao.get(id);
        return shortenedLinkMapper.mapToDto(link);
    }

    public LinkDto createShortenedLink(final LinkDto linkDto) {
        final Link link = shortenedLinkMapper.map(linkDto);
        shortenedLinkDao.save(link);

        return shortenedLinkMapper.mapToDto(link);
    }

    public LinkDto updateShortenedLink(final String id, final UpdatedLinkDto updatedLinkDto) {
        final Link link = shortenedLinkDao.updateDescription(id, updatedLinkDto.getDescription());
        return shortenedLinkMapper.mapToDto(link);
    }

    public void deleteShortenedLink(final String id) {
        shortenedLinkDao.delete(id);
    }

    public String getOriginalLink(final String id) {
        final Link link = shortenedLinkDao.get(id);

        if (clock.instant().isAfter(Instant.ofEpochSecond(link.getExpireAtInSeconds()))) {
            throw new LinkAlreadyExpiredException(id);
        }

        return link.getOriginalLink();
    }
}
