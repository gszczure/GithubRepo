package org.example;

public record ErrorResponseDto(
        int status,
        String message
) {
}