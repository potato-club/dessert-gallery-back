package com.dessert.gallery.dto.chat.list;

import com.dessert.gallery.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChatRecentMessageDto {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "가게 이름")
    private String storeName;

    @Schema(description = "손님 닉네임")
    private String customerName;

    @Schema(description = "썸네일 메시지")
    private String thumbnailMessage;

    @Schema(description = "메시지 타입")
    private MessageType messageType;

    @Schema(description = "가장 최근 채팅 시간 or 채팅방 생성 시간")
    private String lastChatDatetime;
}
