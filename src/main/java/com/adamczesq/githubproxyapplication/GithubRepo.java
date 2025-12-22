package com.adamczesq.githubproxyapplication;

public record GithubRepo(String name, GithubOwner owner, boolean fork) {}