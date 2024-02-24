package com.dessert.gallery.dto.chat.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatRoomDto {

    @Schema(description = "최대 페이지 수")
    private int maxPage;

    @Schema(description = "채팅방 목록")
    private List<ChatRecentMessageDto> chatList;
}
