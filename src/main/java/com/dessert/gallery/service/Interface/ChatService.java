package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;
import com.dessert.gallery.entity.ChatMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ChatService {

    Long createRoom(RoomCreateDto roomCreateDto, HttpServletRequest request);

    void saveMessage(Long id, ChatMessageDto chatMessage);

    List<ChatMessageDto> getMessages(Long chatRoomId, HttpServletRequest request);

    List<ChatRoomDto> getMyChatRoomsList(int page, HttpServletRequest request);

    void deleteRoom(Long roomId, HttpServletRequest request);

    void deleteExpiredChatMessages();

}
