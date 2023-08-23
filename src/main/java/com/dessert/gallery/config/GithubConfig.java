package com.dessert.gallery.config;

import com.dessert.gallery.error.ErrorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class GithubConfig {

    @Value("${github.secret}")
    private String secret;

    private final RestTemplate restTemplate;

    public void createGithubIssue(String apiUrl, ErrorEntity errorEntity) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + secret);

            HttpEntity<ErrorEntity> requestEntity = new HttpEntity<>(errorEntity, headers);

            restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
