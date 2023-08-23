package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.issue.Issue;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface IssueService {

    ResponseEntity<?> createIssue(Issue issue) throws JsonProcessingException;

}
