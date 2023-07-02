package com.dessert.gallery.service.Jwt;

import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    // RefreshToken, email, IP Address 설정
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
        if (object != null && object instanceof Map) {
            return (Map<String, String>) object;
        }
        return null;
    }

    public boolean isRefreshTokenValid(String token, String ipAddress) {
        Map<String, String> values = getValues(token);
        if (values == null) {
            return false;
        }
        String storedIpAddress = values.get("ipAddress");
        return ipAddress.equals(storedIpAddress);
    }

    public boolean isTokenInBlacklist(String token) {
        if (redisTemplate.hasKey(token)) {
            throw new InvalidTokenException("401_Invalid", ErrorCode.INVALID_TOKEN_EXCEPTION);
        }
        return false;
    }

    public void addTokenToBlacklist(String token, long expiration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, true, expiration, TimeUnit.MILLISECONDS);
    }

    // RefreshToken, email, IP Address 삭제
    public void delValues(String token) {
        redisTemplate.delete(token);
    }

    // key를 통해 Email OTP value 리턴
    public String getEmailOtpData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    // 유효 시간 동안 Email OTP(key, value) 저장
    public void setEmailOtpDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    // Email OTP 값 삭제
    public void deleteEmailOtpData(String key) {
        redisTemplate.delete(key);
    }
}