package com.dessert.gallery.controller;

import com.dessert.gallery.dto.issue.Issue;
import com.dessert.gallery.service.Interface.IssueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/issue")
@Tag(name = "Issue Controller", description = "깃 이슈 API")
public class IssueController {

    private final IssueService issueService;

    @Operation(summary = "깃 이슈 API")
    @PostMapping("")
    public ResponseEntity<?> createIssue(@ModelAttribute Issue issue) throws JsonProcessingException {
        return issueService.createIssue(issue);
    }
}
