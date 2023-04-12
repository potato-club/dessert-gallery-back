package com.dessert.gallery.error.exception;

import com.dessert.gallery.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
