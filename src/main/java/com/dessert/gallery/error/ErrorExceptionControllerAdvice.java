package com.dessert.gallery.error;

import com.dessert.gallery.config.GithubConfig;
import com.dessert.gallery.error.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.GeneratedValue;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ErrorExceptionControllerAdvice {

    private final GithubConfig githubConfig;

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final BadRequestException e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    @ExceptionHandler({UnAuthorizedException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final UnAuthorizedException e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final NotFoundException e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    @ExceptionHandler({DuplicateException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final DuplicateException e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    @ExceptionHandler({InternerServerException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final InternerServerException e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final InvalidTokenException e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    @ExceptionHandler({S3Exception.class})
    public ResponseEntity<ErrorEntity> exceptionHandler(final S3Exception e) {
        ErrorEntity errorEntity = this.sendGithubIssue(e);

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorEntity);
    }

    private ErrorEntity sendGithubIssue(BusinessException e) {
        ErrorEntity errorEntity = ErrorEntity.builder()
                .errorCode(e.getErrorCode().getCode())
                .errorMessage(e.getErrorCode().getMessage())
                .build();

        githubConfig.createGithubIssue("https://github.com/potato-club/dessert-gallery-back/issues", errorEntity);

        return errorEntity;
    }
}
