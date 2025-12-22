package com.adamczesq.githubproxyapplication;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GithubService {
    private final GithubClient client;

    GithubService(GithubClient client) {
        this.client = client;
    }

    List<ProxyRepositoryResponse> getRepositories(String username) {
        return client.fetchUserRepos(username).stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    var branches = client.fetchRepoBranches(repo.owner().login(), repo.name())
                            .stream()
                            .map(branch -> new ProxyBranchResponse(branch.name(), branch.commit().sha()))
                            .toList();

                    return new ProxyRepositoryResponse(repo.name(), repo.owner().login(), branches);
                })
                .toList();
    }
}