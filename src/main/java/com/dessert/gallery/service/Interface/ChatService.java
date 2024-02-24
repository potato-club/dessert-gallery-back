package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.list.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ChatService {

    Long createRoom(RoomCreateDto roomCreateDto, HttpServletRequest request);

    void saveMessage(Long id, ChatMessageDto chatMessage);

    List<ChatMessageDto> getMessages(Long chatRoomId, String time, HttpServletRequest request);

    List<ChatRoomDto> getMyChatRoomsList(int page, HttpServletRequest request);

    void deleteRoom(Long roomId, HttpServletRequest request);

    void deleteExpiredChatMessages();

}
