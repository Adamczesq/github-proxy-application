package com.adamczesq.githubproxyapplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
class GithubController {
    private final GithubService service;

    GithubController(GithubService service) {
        this.service = service;
    }

    @GetMapping(value = "/repositories/{username}", produces = "application/json")
    List<ProxyRepositoryResponse> listRepositories(@PathVariable String username) {
        return service.getRepositories(username);
    }
}