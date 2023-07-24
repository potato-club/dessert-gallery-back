package com.dessert.gallery.error.security;

import com.dessert.gallery.error.ErrorJwtCode;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        String ex = null;
        ErrorJwtCode errorCode;

        if (request.getAttribute("exception") != null) {
            ex = request.getAttribute("exception").toString();
        }

        // 토큰이 없을 때 예외 처리
        if (ex.equals(ErrorJwtCode.INVALID_JWT_TOKEN.toString())) {
            errorCode = ErrorJwtCode.INVALID_JWT_TOKEN;
            setResponse(response, errorCode);
        } else if (ex.equals(ErrorJwtCode.UNSUPPORTED_JWT_TOKEN.toString())) {
            errorCode = ErrorJwtCode.UNSUPPORTED_JWT_TOKEN;
            setResponse(response, errorCode);
        } else if (ex.equals(ErrorJwtCode.JWT_TOKEN_EXPIRED.toString())) {
            errorCode = ErrorJwtCode.JWT_TOKEN_EXPIRED;
            setResponse(response, errorCode);
        } else if (ex.equals(ErrorJwtCode.EMPTY_JWT_CLAIMS.toString())) {
            errorCode = ErrorJwtCode.EMPTY_JWT_CLAIMS;
            setResponse(response, errorCode);
        } else if (ex.equals(ErrorJwtCode.JWT_SIGNATURE_MISMATCH.toString())) {
            errorCode = ErrorJwtCode.JWT_SIGNATURE_MISMATCH;
            setResponse(response, errorCode);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorJwtCode errorCode) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", errorCode.getCode());
        json.put("message", errorCode.getMessage());
        response.getWriter().print(json);
    }
}
