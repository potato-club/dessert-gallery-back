package com.dessert.gallery.error;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "400 Bad Request"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "401", "401 UnAuthorized"),
    NOT_ALLOW_WRITE_EXCEPTION(HttpStatus.UNAUTHORIZED, "401_NOT_ALLOW", "401 UnAuthorized"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "403", "403 Forbidden"),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "404 Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 Internal Server Error"),
    CONFLICT_EXCEPTION(HttpStatus.CONFLICT, "409", "409 Conflict");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
