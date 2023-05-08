package com.jetbrains.akaluzhonak.urlshortener.dao;

import com.jetbrains.akaluzhonak.urlshortener.model.dao.Link;

import java.util.List;

public interface ShortenedLinkDao {

    List<Link> getAll();

    Link get(final String id);

    void save(final Link link);

    Link updateDescription(final String id, final String description);

    void delete(final String id);
}
