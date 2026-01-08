package com.adamczesq.githubproxyapplication;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
class GithubService {
    private final GithubClient client;

    GithubService(GithubClient client) {
        this.client = client;
    }

    List<ProxyRepositoryResponse> getRepositories(String username) {
        List<CompletableFuture<ProxyRepositoryResponse>> futures = client.fetchUserRepos(username).stream()
                .filter(repo -> !repo.fork())
                .map(repo -> CompletableFuture.supplyAsync(() -> {
                    var branches = client.fetchRepoBranches(repo.owner().login(), repo.name())
                            .stream()
                            .map(branch -> new ProxyBranchResponse(branch.name(), branch.commit().sha()))
                            .toList();

                    return new ProxyRepositoryResponse(repo.name(), repo.owner().login(), branches);
                }))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}