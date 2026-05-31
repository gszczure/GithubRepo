package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryDto(
        String name,
        boolean fork,
        GithubOwnerDto owner
) {
}