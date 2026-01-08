package com.adamczesq.githubproxyapplication;

import java.util.List;

record GithubRepo(String name, GithubOwner owner, boolean fork) {}
record GithubOwner(String login) {}
record GithubBranch(String name, GithubCommit commit) {}
record GithubCommit(String sha) {}

record ProxyRepositoryResponse(String repositoryName, String ownerLogin, List<ProxyBranchResponse> branches) {}
record ProxyBranchResponse(String name, String lastCommitSha) {}

record ErrorResponse(int status, String message) {}