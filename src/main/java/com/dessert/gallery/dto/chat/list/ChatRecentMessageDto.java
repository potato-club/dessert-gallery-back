package com.dessert.gallery.dto.chat.list;

import com.dessert.gallery.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
public class ChatRecentMessageDto {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "가게 ID")
    private Long storeId;

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

    @Builder
    public ChatRecentMessageDto(Long roomId, Long storeId, String storeName, String customerName,
                                String thumbnailMessage, MessageType messageType, String lastChatDatetime) {
        this.roomId = roomId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.customerName = customerName;
        this.thumbnailMessage = thumbnailMessage;
        this.messageType = messageType;
        this.lastChatDatetime = lastChatDatetime;
    }
}
