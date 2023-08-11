package com.dessert.gallery.dto.chat;

import com.dessert.gallery.entity.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessageDto {

    @Schema(description = "채팅 메시지")
    private Long roomId;

    @Schema(description = "채팅 메시지")
    private String message;

    @Schema(description = "작성 시간")
    private LocalDateTime timestamp;

    public ChatMessage toEntity() {
        ChatMessage chatMessage = ChatMessage.builder()
                .message(message)
                .timestamp(timestamp)
                .build();

        return chatMessage;
    }
}
