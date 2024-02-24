package com.dessert.gallery.dto.chat;

import com.dessert.gallery.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
public class MessageStatusDto {

    @Schema(description = "채팅방 ID")
    private Long chatRoomId;

    @Schema(description = "보낸 사람")
    private String sender;

    @Schema(description = "채팅 메시지")
    private String message;

    @Schema(description = "메시지 타입")
    private MessageType messageType;

    @Schema(description = "글쓴 시간")
    private String dateTime;

    public MessageStatusDto(Long chatRoomId, String sender, String message, MessageType messageType, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        this.chatRoomId = chatRoomId;
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
        this.dateTime = dateTime.format(formatter);
    }
}
