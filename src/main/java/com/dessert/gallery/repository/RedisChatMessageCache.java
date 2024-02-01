package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisChatMessageCache {

    private final RedisTemplate<String, LinkedList<ChatMessageDto>> redisTemplate;

    public void put(Long roomId, Queue<ChatMessageDto> messageQueue) {
        String key = generateKey(roomId);
        redisTemplate.opsForValue().set(key, new LinkedList<>(messageQueue));
        redisTemplate.expire(key, 40, TimeUnit.DAYS);
    }

    public boolean containsKey(Long roomId) {
        String key = generateKey(roomId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public LinkedList<ChatMessageDto> get(Long roomId) {
        return redisTemplate.opsForValue().get(generateKey(roomId));
    }

    public Queue<ChatMessageDto> values() {
        String randomKey = redisTemplate.randomKey();
        if (randomKey != null) {
            return get(Long.valueOf(randomKey));
        }
        return new LinkedList<>();
    }

    public void deleteOldMessages() {
        LocalDate currentDate = LocalDate.now().minusDays(30);
        String pattern = generateKeyPattern(currentDate);
        Set<String> keysToDelete = redisTemplate.keys(pattern);

        assert keysToDelete != null;
        redisTemplate.delete(keysToDelete);
    }

    public void deleteAll() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
    }

    private String generateKey(Long roomId) {
        LocalDate currentDate = LocalDate.now();
        return roomId.toString() + ":" + currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String generateKeyPattern(LocalDate date) {
        return "*:" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

