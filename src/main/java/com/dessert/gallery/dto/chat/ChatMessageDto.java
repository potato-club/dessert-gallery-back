package com.dessert.gallery.dto.chat;

import com.dessert.gallery.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageDto {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "보낸 사람")
    private String sender;

    @Schema(description = "채팅 메시지")
    private String message;

    @Schema(description = "메시지 타입")
    private MessageType messageType;

    @Schema(description = "글쓴 시간")
    private String dateTime;
}
