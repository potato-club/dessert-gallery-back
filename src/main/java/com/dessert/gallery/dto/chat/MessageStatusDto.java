package com.dessert.gallery.dto.chat;

import com.dessert.gallery.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusDto {

    @Schema(description = "채팅방 ID")
    private Long chatRoomId;

    @Schema(description = "보낸 사람")
    private String sender;

    @Schema(description = "채팅 메시지")
    private String message;

    @Schema(description = "메시지 타입", example = "CHAT")
    private MessageType messageType;

    @Schema(description = "글쓴 시간", example = "yyyy-MM-dd HH:mm:ss")
    private String dateTime;
}
