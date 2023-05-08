package com.jetbrains.akaluzhonak.urlshortener.config;

import com.jetbrains.akaluzhonak.urlshortener.exception.InternalServerException;
import com.jetbrains.akaluzhonak.urlshortener.exception.LinkAlreadyExpiredException;
import com.jetbrains.akaluzhonak.urlshortener.exception.NotFoundException;
import com.jetbrains.akaluzhonak.urlshortener.model.dto.ErrorDto;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.RequestLimitExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class, ResourceNotFoundException.class})
    public ErrorDto handleNotFound(Exception e) {
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler({ProvisionedThroughputExceededException.class, RequestLimitExceededException.class})
    public ErrorDto handleLimitExceededException(Exception e) {
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public ErrorDto handleInternalException(InternalServerException e) {
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class})
    public ErrorDto handleValidationException(ValidationException e) {
        logger.error("Validation Exception in controller", e);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler({LinkAlreadyExpiredException.class})
    public ResponseEntity<ErrorDto> handleLinkAlreadyExpiredException(LinkAlreadyExpiredException e) {
        return ResponseEntity.status(498)
                .body(new ErrorDto(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleInternalError(Exception e) {
        logger.error("Unhandled Exception in controller", e);
        return new ErrorDto("Unexpected server error");
    }
}
