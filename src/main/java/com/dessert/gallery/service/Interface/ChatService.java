package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ChatService {

    Long createRoom(RoomCreateDto roomCreateDto, HttpServletRequest request);

    void saveChatMessage(Long id, ChatMessageDto chatMessage);

    List<ChatMessageDto> getLastChatMessages(Long chatRoomId, HttpServletRequest request);

    List<ChatRoomDto> getMyChatRoomsList(int page, HttpServletRequest request);

    void deleteRoom(Long roomId, HttpServletRequest request);

    void deleteExpiredChatMessages();
}
