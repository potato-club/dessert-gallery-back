package com.dessert.gallery.error.exception;

import com.dessert.gallery.error.ErrorCode;

public class InternerServerException extends BusinessException {

    public InternerServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
