package com.adamczesq.githubproxyapplication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GithubProxyApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private static WireMockServer wireMockServer;

	@BeforeAll
	static void startWireMock() {
		wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
		wireMockServer.start();
		WireMock.configureFor(wireMockServer.port());
	}

	@AfterAll
	static void stopWireMock() {
		wireMockServer.stop();
	}

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("github.api.url", wireMockServer::baseUrl);
	}

	@Test
	void shouldReturnUserRepositoriesIncludingBranches() throws Exception {
		stubFor(WireMock.get("/users/testuser/repos")
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                    {"name": "my-repo", "owner": {"login": "testuser"}, "fork": false},
                                    {"name": "forked-repo", "owner": {"login": "testuser"}, "fork": true}
                                ]
                                """)));

		stubFor(WireMock.get("/repos/testuser/my-repo/branches")
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                    {"name": "main", "commit": {"sha": "111abc"}}
                                ]
                                """)));

		mockMvc.perform(get("/api/v1/repositories/testuser")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].repositoryName").value("my-repo"))
				.andExpect(jsonPath("$[0].branches[0].name").value("main"))
				.andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("111abc"));
	}

	@Test
	void shouldReturn404ForNonExistingUser() throws Exception {
		stubFor(WireMock.get("/users/nonexistent/repos")
				.willReturn(aResponse()
						.withStatus(404)
						.withHeader("Content-Type", "application/json")
						.withBody("{\"message\": \"Not Found\"}")));

		mockMvc.perform(get("/api/v1/repositories/nonexistent")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").exists());
	}
}