package org.example;

import org.junit.jupiter.api.*;
import tools.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "github.api.url=http://localhost:8089")
class GithubIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        configureFor("localhost", 8089);
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Should return only non fork repositories with branches")
    void shouldReturnRepositoriesWhenGithubUserExists() {

        stubFor(get(urlEqualTo("/users/john/repos"))
                .willReturn(okJson(objectMapper.writeValueAsString(List.of(
                        createRepositoryDto("test-repo-one", false),
                        createRepositoryDto("test-forked-repo", true)))))
        );

        stubFor(get(urlEqualTo("/repos/john/test-repo-one/branches"))
                .willReturn(okJson(objectMapper.writeValueAsString(List.of(
                        createBranchDto("main", "abc123")))))
        );

        given()
                .when()
                .get("/api/github/john/repositories")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].repositoryName", equalTo("test-repo-one"))
                .body("[0].ownerLogin", equalTo("john"))
                .body("[0].branches.size()", equalTo(1))
                .body("[0].branches[0].name", equalTo("main"))
                .body("[0].branches[0].lastCommitSha", equalTo("abc123"));
    }

    @Test
    @DisplayName("Should return 404 when github user does not exist")
    void shouldReturn404WhenGithubUserDoesNotExist() {

        stubFor(get(urlEqualTo("/users/unknown/repos"))
                .willReturn(aResponse().withStatus(404)));

        given()
                .when()
                .get("/api/github/unknown/repositories")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("message", equalTo("GitHub user 'unknown' not found"));
    }

    private GithubRepositoryDto createRepositoryDto(String name, boolean fork) {
        return new GithubRepositoryDto(
                name,
                fork,
                new GithubOwnerDto("john")
        );
    }

    private GithubBranchDto createBranchDto(String name, String sha) {
        return new GithubBranchDto(
                name,
                new GithubCommitDto(sha)
        );
    }
}