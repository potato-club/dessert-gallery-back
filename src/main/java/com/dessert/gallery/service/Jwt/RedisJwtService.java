package com.dessert.gallery.service.Jwt;

import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.InvalidTokenException;
import com.dessert.gallery.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtService {

    private final RedisTemplate<String, Object> redisTemplate;

    // RefreshToken, email 설정
    public void setValues(String token, String email) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        operations.set(token, map, Duration.ofDays(7)); // 7일 뒤 메모리에서 삭제됨
    }

    // 키값으로 벨류 가져오기
    public Map<String, String> getValues(String token){
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Object object = operations.get(token);
        if (object instanceof Map) {
            return (Map<String, String>) object;
        }
        return null;
    }

    public boolean isRefreshTokenValid(String token) {
        Map<String, String> values = getValues(token);
        return values != null;
    }

    public boolean isTokenInBlacklist(String token) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
            throw new InvalidTokenException("401_Invalid", ErrorCode.INVALID_TOKEN_EXCEPTION);
        }

        return false;
    }

    public void addTokenToBlacklist(String token, long expiration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, true, expiration, TimeUnit.MILLISECONDS);
    }

    // RefreshToken, email 삭제
    public void delValues(String token) {
        redisTemplate.delete(token);
    }

    // key를 통해 Email OTP value 리턴
    public Object getEmailOtpData(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object value = valueOperations.get(key);
        if (value == null) {
            throw new NotFoundException("Email OTP not found for key: " + key, ErrorCode.NOT_FOUND_EXCEPTION);
        }
        return value;
    }

    // 유효 시간 동안 Email OTP(key, value) 저장
    public void setEmailOtpDataExpire(String key, String value, long duration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    // Email OTP 값 삭제
    public void deleteEmailOtpData(String key) {
        redisTemplate.delete(key);
    }

    // 기존의 OTP 코드가 있는지 확인하고 있다면 삭제
    public void deleteExistingOtp(String email) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(email))) {
            redisTemplate.delete(email);
        }
    }
}
