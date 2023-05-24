package com.dessert.gallery.error.exception;

import com.dessert.gallery.error.ErrorCode;

public class S3Exception extends BusinessException {
    public S3Exception(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
