package com.jetbrains.akaluzhonak.urlshortener.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {
    private String message;
}
