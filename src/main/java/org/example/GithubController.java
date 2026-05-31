package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
class GithubController {

    private final GithubService githubService;

    @GetMapping("/{username}/repositories")
    public ResponseEntity<List<RepositoryDto>> getRepositories(@PathVariable String username) {
        return ResponseEntity
                .ok(githubService.getRepositories(username));
    }
}