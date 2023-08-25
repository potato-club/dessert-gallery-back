package com.dessert.gallery.error;

import com.dessert.gallery.config.GithubConfig;
import com.dessert.gallery.dto.issue.Issue;
import com.dessert.gallery.enums.DeveloperType;
import com.dessert.gallery.error.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
public class ErrorExceptionControllerAdvice {

    @Value("${github.apiUrl_back}")
    private String apiUrlBack;

    private final GithubConfig githubConfig;
    private final RestTemplate restTemplate;

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final BadRequestException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({UnAuthorizedException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final UnAuthorizedException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final NotFoundException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({DuplicateException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final DuplicateException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({InternerServerException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(HttpServletRequest request, final InternerServerException e)
            throws JsonProcessingException {

        String errorMessage = "Error code: " + e.getErrorCode().getCode() + "\nError message: " + e.getErrorCode().getMessage();
        List<String> labels = new ArrayList<>();
        labels.add("bug");
        labels.add(DeveloperType.B.getTitle());

        Issue issue = Issue.builder()
                .title(request.getRequestURI())
                .body(errorMessage)
                .labels(labels)
                .build();

        String jsonIssue = new ObjectMapper().writeValueAsString(issue);
        HttpHeaders headers = githubConfig.githubApiHeaders(DeveloperType.B);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonIssue, headers);

        restTemplate.exchange(apiUrlBack, HttpMethod.POST, httpEntity, String.class);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final InvalidTokenException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({S3Exception.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final S3Exception e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorEntity.builder()
                        .errorCode(e.getErrorCode().getCode())
                        .errorMessage(e.getErrorCode().getMessage())
                        .build());
    }
}
