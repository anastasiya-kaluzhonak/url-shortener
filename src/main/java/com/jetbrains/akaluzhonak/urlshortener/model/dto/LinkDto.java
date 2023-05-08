package com.jetbrains.akaluzhonak.urlshortener.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LinkDto {

    private String id;
    @NotBlank
    private String originalLink;
    @Size(max = 256)
    private String description;
    private LocalDateTime createdAt;
}
