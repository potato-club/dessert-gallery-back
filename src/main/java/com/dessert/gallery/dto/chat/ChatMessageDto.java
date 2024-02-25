package com.dessert.gallery.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ChatMessageDto {

    @Schema(description = "현재를 제외한 최근 채팅 날짜")
    private String lastDatetime;

    @Schema(description = "채팅 내역")
    private List<MessageStatusDto> chatList;
}
