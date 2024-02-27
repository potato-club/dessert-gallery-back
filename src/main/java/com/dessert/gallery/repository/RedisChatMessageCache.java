package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.dto.chat.list.RedisRecentChatDto;
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

    private final RedisTemplate<String, Object> redisSimpleTemplate;
    private final RedisTemplate<String, LinkedList<MessageStatusDto>> redisTemplate;
    private final RedisTemplate<String, ArrayList<RedisRecentChatDto>> redisChatTemplate;

    public void put(Long roomId, Deque<MessageStatusDto> messageDeque) {
        LocalDate currentDate = LocalDate.now();
        String key = generateKey(roomId, currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        redisTemplate.opsForValue().set(key, new LinkedList<>(messageDeque));
        redisTemplate.expire(key, 40, TimeUnit.DAYS);
    }

    public void putChatList(String uid, RedisRecentChatDto redisRecentChatDto) {

        ArrayList<RedisRecentChatDto> list = new ArrayList<>();

        if (Boolean.TRUE.equals(redisChatTemplate.hasKey(uid))) {
            list = redisChatTemplate.opsForValue().get(uid);

            assert list != null;
            boolean isDuplicate = false;

            for (RedisRecentChatDto chatDto : list) {
                if (chatDto.getRoomId().equals(redisRecentChatDto.getRoomId())) {
                    // 중복된 roomId가 있는 경우 수정
                    chatDto.setThumbnailMessage(redisRecentChatDto.getThumbnailMessage());
                    chatDto.setMessageType(redisRecentChatDto.getMessageType());
                    chatDto.setDateTime(redisRecentChatDto.getDateTime());

                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                // 중복된 roomId가 없는 경우 추가
                list.add(redisRecentChatDto);
            }

            Collections.sort(list);
        } else {
            list.add(redisRecentChatDto);
        }

        redisChatTemplate.opsForValue().set(uid, list);
        redisChatTemplate.expire(uid, 30, TimeUnit.DAYS);
    }

    public void putRoomIdForUid(Long roomId, String uid) {
        redisSimpleTemplate.opsForValue().set(roomId.toString(), uid);
        redisSimpleTemplate.expire(roomId.toString(), 30, TimeUnit.DAYS);
    }

    public boolean containsKey(Long roomId, String time) {
        String key = generateKey(roomId, time);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public LinkedList<MessageStatusDto> get(Long roomId, String time) {
        redisSimpleTemplate.expire(roomId.toString(), 30, TimeUnit.DAYS);   // 채팅 조회 시 관련 정보 expire 시간 초기화

        String uid = getUid(roomId);
        redisChatTemplate.expire(uid, 30, TimeUnit.DAYS);

        return redisTemplate.opsForValue().get(generateKey(roomId, time));
    }

    public String getUid(Long roomId) {
        redisSimpleTemplate.expire(roomId.toString(), 30, TimeUnit.DAYS);
        return Objects.requireNonNull(redisSimpleTemplate.opsForValue().get(roomId.toString())).toString();
    }

    public ArrayList<RedisRecentChatDto> getChatList(String uid) {
        redisChatTemplate.expire(uid, 30, TimeUnit.DAYS);
        return redisChatTemplate.opsForValue().get(uid);
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

    public void deleteChatRoom(Long roomId, String uid) {
        String pattern = roomId.toString() + ":*";
        Set<String> keysToDelete = redisTemplate.keys(pattern);

        assert keysToDelete != null;
        redisTemplate.delete(keysToDelete);
        redisSimpleTemplate.delete(roomId.toString());
        redisChatTemplate.delete(uid);
    }

    public void deleteAll() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
        Objects.requireNonNull(redisChatTemplate.getConnectionFactory()).getConnection().flushDb();
        Objects.requireNonNull(redisSimpleTemplate.getConnectionFactory()).getConnection().flushDb();
    }

    private String generateKey(Long roomId, String time) {
        return roomId.toString() + ":" + time;
    }

    private String generateKeyPattern(LocalDate date) {
        return "*:" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

