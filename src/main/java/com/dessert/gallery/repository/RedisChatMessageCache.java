package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisChatMessageCache {

    private final RedisTemplate<String, LinkedList<ChatMessageDto>> redisTemplate;

    public void put(Long roomId, Queue<ChatMessageDto> messageQueue) {
        LocalDate currentDate = LocalDate.now();
        String key = generateKey(roomId, currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        redisTemplate.opsForValue().set(key, new LinkedList<>(messageQueue));
        redisTemplate.expire(key, 40, TimeUnit.DAYS);
    }

    public boolean containsKey(Long roomId, String time) {
        String key = generateKey(roomId, time);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public LinkedList<ChatMessageDto> get(Long roomId, String time) {
        return redisTemplate.opsForValue().get(generateKey(roomId, time));
    }

    public void deleteOldMessages() {
        LocalDate currentDate = LocalDate.now().minusDays(30);
        String pattern = generateKeyPattern(currentDate);
        Set<String> keysToDelete = redisTemplate.keys(pattern);

        assert keysToDelete != null;
        redisTemplate.delete(keysToDelete);
    }

//    public void deleteAll() {
//        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
//    }

    private String generateKey(Long roomId, String time) {
        return roomId.toString() + ":" + time;
    }

    private String generateKeyPattern(LocalDate date) {
        return "*:" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

