package com.dessert.gallery.dto.chat;

import com.dessert.gallery.entity.ChatMessage;
import com.dessert.gallery.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageDto {

    @Schema(description = "채팅방 ID")
    private Long id;

    @Schema(description = "보낸 사람")
    private String sender;

    @Schema(description = "채팅 메시지")
    private String message;

    @Schema(hidden = true)
    private ChatRoom chatRoom;

    public ChatMessage toEntity() {
        LocalDateTime localDateTime = LocalDateTime.now();

        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .message(message)
                .sender(sender)
                .timestamp(localDateTime)
                .build();
    }
}
