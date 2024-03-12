package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.MessageStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisChatMessageCache {

    private final RedisTemplate<String, LinkedList<MessageStatusDto>> redisTemplate;

    public void put(Long roomId, Deque<MessageStatusDto> messageDeque) {
        LocalDate currentDate = LocalDate.now();
        String key = generateKey(roomId, currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        redisTemplate.opsForValue().set(key, new LinkedList<>(messageDeque));
        redisTemplate.expire(key, 40, TimeUnit.DAYS);
    }

    public boolean isContainsKey(Long roomId, String time) {
        String key = generateKey(roomId, time);
        return !Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public LinkedList<MessageStatusDto> get(Long roomId, String time) {
        return redisTemplate.opsForValue().get(generateKey(roomId, time));
    }

    public MessageStatusDto getRecentChatData(Long roomId) {
        Set<String> keys = redisTemplate.keys(roomId + ":*");

        assert keys != null;
        List<String> sortedKeys = new ArrayList<>(keys);

        if (sortedKeys.isEmpty()) {
            return new MessageStatusDto();
        }

        sortedKeys.sort((dateTime1, dateTime2) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String dateString1 = dateTime1.split(":")[1];
            String dateString2 = dateTime2.split(":")[1];

            LocalDate time1 = LocalDate.parse(dateString1, formatter);
            LocalDate time2 = LocalDate.parse(dateString2, formatter);

            return time2.compareTo(time1);
        });

        String time = sortedKeys.get(0).split(":")[1];
        LinkedList<MessageStatusDto> messageStatusDto = redisTemplate.opsForValue().get(generateKey(roomId, time));

        return messageStatusDto == null ? new MessageStatusDto() : messageStatusDto.getLast();
    }

    public String getLastChatDateTime(Long roomId, String recentDateTime) {
        Set<String> keys = redisTemplate.keys(roomId + ":*");

        assert keys != null;
        List<String> sortedKeys = new ArrayList<>(keys);

        Collections.sort(sortedKeys);
        int index = sortedKeys.indexOf(roomId + ":" + recentDateTime);

        if (index > 0) {
            return sortedKeys.get(index - 1);
        } else {
            return null;
        }
    }

    public void deleteOldMessages() {
        LocalDate currentDate = LocalDate.now().minusDays(30);
        String pattern = generateKeyPattern(currentDate);
        Set<String> keysToDelete = redisTemplate.keys(pattern);

        assert keysToDelete != null;
        redisTemplate.delete(keysToDelete);
    }

    public void deleteChatRoom(Long roomId) {
        String pattern = roomId.toString() + ":*";
        Set<String> keysToDelete = redisTemplate.keys(pattern);

        assert keysToDelete != null;
        redisTemplate.delete(keysToDelete);
    }

    public void deleteAll() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
    }

    private String generateKey(Long roomId, String time) {
        return roomId.toString() + ":" + time;
    }

    private String generateKeyPattern(LocalDate date) {
        return "*:" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

