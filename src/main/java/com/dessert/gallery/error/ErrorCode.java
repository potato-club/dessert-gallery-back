package com.dessert.gallery.error;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "400 Bad Request"),
    PARAMETER_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "잘못된 파라미터 값"),
    SCHEDULE_EXCEPTION_1(HttpStatus.BAD_REQUEST, "S0001", "리뷰 등록된 스케줄"),
    SCHEDULE_EXCEPTION_2(HttpStatus.BAD_REQUEST, "S0002", "삭제 불가능한 예약 스케줄"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "401", "401 UnAuthorized"),
    KAKAO_ACCESS_TOKEN_FAILED(HttpStatus.UNAUTHORIZED, "K4001", "Failed to get access token!"),
    KAKAO_USER_INFO_FAILED(HttpStatus.UNAUTHORIZED, "K4002", "Failed to get user info!"),
    ACCESS_DENIED_BLACKLIST_EXCEPTION(HttpStatus.UNAUTHORIZED, "F4001", "401 BlackList"),
    ACCESS_DENIED_MANAGER_EXCEPTION(HttpStatus.UNAUTHORIZED, "F4002", "401 Not Access Manager"),
    ACCESS_DENIED_USER_EXCEPTION(HttpStatus.UNAUTHORIZED, "F4002", "401 Not Access User"),
    NOT_ALLOW_WRITE_EXCEPTION(HttpStatus.UNAUTHORIZED, "401_NOT_ALLOW", "401 UnAuthorized"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "403", "403 Forbidden"),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "404 Not Found"),
    CONFLICT_EXCEPTION(HttpStatus.CONFLICT, "409", "409 Conflict"),
    INVALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "401_Invalid", "Invalid access: token in blacklist"),
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "500", "500 Server Error");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
