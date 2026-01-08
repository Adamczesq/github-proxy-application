package com.adamczesq.githubproxyapplication;

record GithubRepo(String name, GithubOwner owner, boolean fork) {}