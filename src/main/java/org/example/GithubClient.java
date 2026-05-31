package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
class GithubClient {

    private final RestClient githubRestClient;

    List<GithubRepositoryDto> getRepositories(String username) {
        return githubRestClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new GithubUserNotFoundException(username);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    List<GithubBranchDto> getBranches(String owner, String repository) {
        return githubRestClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repository)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}