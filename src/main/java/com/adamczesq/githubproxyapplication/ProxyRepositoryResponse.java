package com.adamczesq.githubproxyapplication;

import java.util.List;

record ProxyRepositoryResponse(String repositoryName, String ownerLogin, List<ProxyBranchResponse> branches) {}