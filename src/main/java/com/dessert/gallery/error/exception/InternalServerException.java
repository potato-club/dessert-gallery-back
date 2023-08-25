package com.dessert.gallery.error.exception;

import com.dessert.gallery.error.ErrorCode;

public class InternalServerException extends BusinessException {

    public InternalServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
