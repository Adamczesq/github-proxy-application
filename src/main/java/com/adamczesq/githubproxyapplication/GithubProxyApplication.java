package com.adamczesq.githubproxyapplication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class GithubProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubProxyApplication.class, args);
	}

	@Bean
	RestClient restClient(@Value("${github.api.url:https://api.github.com}") String baseUrl) {
		return RestClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader("Accept", "application/vnd.github.v3+json")
				.build();
	}
}