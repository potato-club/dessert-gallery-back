package com.dessert.gallery.jwt;

import com.dessert.gallery.error.ErrorJwtCode;
import com.dessert.gallery.service.Jwt.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String ipAddress = request.getRemoteAddr();

        if (path.contains("/swagger") || path.contains("/v3/api-docs")) {   // 추후 ADMIN 권한을 가진 사람만 접근할 수 있도록 변경 예정.
            filterChain.doFilter(request, response);
            return;
        }

        if (path.contains("/users/login") || path.contains("/users/signup") || path.contains("/users/mail")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        ErrorJwtCode errorCode;

        try {
            if (accessToken == null) {
                String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
                if (jwtTokenProvider.validateToken(refreshToken) && redisService.isRefreshTokenValid(refreshToken, ipAddress)
                    && path.contains("/reissue")) {
                    filterChain.doFilter(request, response);
                }
            } else {
                if (jwtTokenProvider.validateToken(accessToken) && !redisService.isTokenInBlacklist(accessToken)) {
                    this.setAuthentication(accessToken);
                }
            }
        } catch (MalformedJwtException e) {
            errorCode = ErrorJwtCode.INVALID_JWT_TOKEN;
            setResponse(response, errorCode);
            return;
        } catch (ExpiredJwtException e) {
            errorCode = ErrorJwtCode.JWT_TOKEN_EXPIRED;
            setResponse(response, errorCode);
            return;
        } catch (UnsupportedJwtException e) {
            errorCode = ErrorJwtCode.UNSUPPORTED_JWT_TOKEN;
            setResponse(response, errorCode);
            return;
        } catch (IllegalArgumentException e) {
            errorCode = ErrorJwtCode.EMPTY_JWT_CLAIMS;
            setResponse(response, errorCode);
            return;
        } catch (SignatureException e) {
            errorCode = ErrorJwtCode.JWT_SIGNATURE_MISMATCH;
            setResponse(response, errorCode);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setResponse(HttpServletResponse response, ErrorJwtCode errorCode) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", errorCode.getCode());
        json.put("message", errorCode.getMessage());
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    private void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
