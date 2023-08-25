package com.dessert.gallery.service.Impl;

import com.dessert.gallery.config.GithubConfig;
import com.dessert.gallery.dto.issue.Issue;
import com.dessert.gallery.service.Interface.IssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {
    @Value("${github.apiUrl_front}")
    private String apiUrl;
    private final GithubConfig githubConfig;
    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<?> createIssue(Issue issue) throws JsonProcessingException {
        String jsonIssue = new ObjectMapper().writeValueAsString(issue);
        HttpHeaders headers = githubConfig.githubApiHeaders(issue.getType());
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonIssue, headers);

        return restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, String.class);
    }
}
