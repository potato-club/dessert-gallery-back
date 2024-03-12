package com.dessert.gallery.service.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ChatPreHandler implements ChannelInterceptor {

    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct // 의존성 주입 후, 초기화를 수행
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        // 헤더 토큰 얻기
        String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));
        String command = String.valueOf(headerAccessor.getHeader("stompCommand"));

        if (!command.equals("SEND")) {
            return message;
        }

        if (authorizationHeader == null || authorizationHeader.equals("null")) {
            throw new MalformedJwtException("MalformedJwtException");
        }

        String token = authorizationHeader.replace("[bearer ", "").replace("]", "");

        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            boolean check = !claims.getBody().getExpiration().before(new Date());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("MalformedJwtException");
        } catch (ExpiredJwtException e) {
            throw new MessageDeliveryException("ExpiredJwtException");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("UnsupportedJwtException");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("IllegalArgumentException");
        } catch (SignatureException e) {
            throw new SignatureException("SignatureException");
        }

        return message;
    }
}
