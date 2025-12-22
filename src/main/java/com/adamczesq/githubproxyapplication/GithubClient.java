package com.adamczesq.githubproxyapplication;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GithubClient {
    private final RestClient restClient;

    GithubClient(RestClient restClient) {
        this.restClient = restClient;
    }

    List<GithubRepo> fetchUserRepos(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }

    List<GithubBranch> fetchRepoBranches(String owner, String repoName) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repoName)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }
}