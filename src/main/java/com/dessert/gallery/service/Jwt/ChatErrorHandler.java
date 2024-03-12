package com.dessert.gallery.service.Jwt;

import com.dessert.gallery.error.ErrorJwtCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class ChatErrorHandler extends StompSubProtocolErrorHandler {

    public ChatErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, Throwable ex) {

        if (ex.getCause().getMessage().equals("MalformedJwtException")) {
            return handleMalformedJwtException();
        }

        if (ex.getCause().getMessage().equals("ExpiredJwtException")) {
            return handleExpiredJwtException();
        }

        if (ex.getCause().getMessage().equals("UnsupportedJwtException")) {
            return handleUnsupportedJwtException();
        }

        if (ex.getCause().getMessage().equals("IllegalArgumentException")) {
            return handleIllegalArgumentException();
        }

        if (ex.getCause().getMessage().equals("SignatureException")) {
            return handleSignatureException();
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleExpiredJwtException() {
        return prepareErrorMessage(ErrorJwtCode.JWT_TOKEN_EXPIRED, ErrorJwtCode.JWT_TOKEN_EXPIRED.getMessage());
    }

    private Message<byte[]> handleMalformedJwtException() {
        return prepareErrorMessage(ErrorJwtCode.INVALID_JWT_TOKEN, ErrorJwtCode.INVALID_JWT_TOKEN.getMessage());
    }

    private Message<byte[]> handleUnsupportedJwtException() {
        return prepareErrorMessage(ErrorJwtCode.UNSUPPORTED_JWT_TOKEN, ErrorJwtCode.UNSUPPORTED_JWT_TOKEN.getMessage());
    }

    private Message<byte[]> handleIllegalArgumentException() {
        return prepareErrorMessage(ErrorJwtCode.INVALID_JWT_TOKEN, ErrorJwtCode.INVALID_JWT_TOKEN.getMessage());
    }

    private Message<byte[]> handleSignatureException() {
        return prepareErrorMessage(ErrorJwtCode.JWT_SIGNATURE_MISMATCH, ErrorJwtCode.JWT_SIGNATURE_MISMATCH.getMessage());
    }

    private Message<byte[]> prepareErrorMessage(ErrorJwtCode errorJwtCode, String errorMessage) {
        String message = errorJwtCode.getMessage();

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(errorMessage);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(message.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
