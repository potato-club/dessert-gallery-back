package com.dessert.gallery.dto.chat.list;

import com.dessert.gallery.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class RedisRecentChatDto implements Comparable<RedisRecentChatDto> {

    @Schema(description = "Room Id")
    private Long roomId;

    @Schema(description = "썸네일 메시지")
    private String thumbnailMessage;

    @Schema(description = "메시지 타입")
    private MessageType messageType;

    @Schema(description = "저장된 시간")
    private String timestamp;

    @Override
    public int compareTo(RedisRecentChatDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time1 = LocalDateTime.parse(timestamp, formatter);
        LocalDateTime time2 = LocalDateTime.parse(dto.getTimestamp(), formatter);

        if (time1.isBefore(time2)) {
            return -1;
        } else {
            return 1;
        }
    }
}
