package com.jetbrains.akaluzhonak.urlshortener.exception;

public class LinkAlreadyExpiredException extends RuntimeException {

    public static final String ERROR_MESSAGE = "Link with id=%s has already expired";

    public LinkAlreadyExpiredException(String linkId) {
        super(String.format(ERROR_MESSAGE, linkId));
    }
}
