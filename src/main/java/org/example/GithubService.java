package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class GithubService {

    private final GithubClient githubClient;

    public List<RepositoryDto> getRepositories(String username) {
        return githubClient.getRepositories(username)
                .stream()
                .filter(repository -> !repository.fork())
                .map(this::mapToRepositoryDto)
                .toList();
    }

    private RepositoryDto mapToRepositoryDto(GithubRepositoryDto repository) {
        List<BranchDto> branches = githubClient.getBranches(repository.owner().login(), repository.name())
                .stream()
                .map(branch -> new BranchDto(
                        branch.name(),
                        branch.commit().sha()))
                .toList();

        return new RepositoryDto(
                repository.name(),
                repository.owner().login(),
                branches
        );
    }
}