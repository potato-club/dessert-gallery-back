package com.dessert.gallery.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDto {

    @Schema(description = "채팅방 ID")
    private Long roomId;

    @Schema(description = "가게 이름")
    private String storeName;

    @Schema(description = "손님 닉네임")
    private String customerName;

    @Schema(description = "썸네일 메시지")
    private String thumbnailMessage;
}
