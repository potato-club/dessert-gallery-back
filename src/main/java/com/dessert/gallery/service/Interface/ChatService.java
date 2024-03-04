package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.dto.chat.list.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;

import javax.servlet.http.HttpServletRequest;

public interface ChatService {

    Long createRoom(Long storeId, HttpServletRequest request);

    void saveMessage(Long id, MessageStatusDto messageStatusDto, HttpServletRequest request);

    ChatMessageDto getMessages(Long chatRoomId, String time, HttpServletRequest request);

    ChatRoomDto getMyChatRoomsList(int page, HttpServletRequest request);

    void deleteRoom(Long roomId, HttpServletRequest request);

    void deleteExpiredChatMessages();

}
