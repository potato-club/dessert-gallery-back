package com.dessert.gallery.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class GithubConfig {

    @Value("${github.secret}")
    private String secret;

    @Bean
    public HttpHeaders githubApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Charset", "UTF-8");
        return headers;
    }
}
