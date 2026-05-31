package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class RestClientConfig {

    @Bean
    RestClient githubRestClient(
            @Value("${github.api.url:https://api.github.com}")
            String githubApiUrl
    ) {
        return RestClient.builder()
                .baseUrl(githubApiUrl)
                .build();
    }
}