package com.dessert.gallery.controller;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;
import com.dessert.gallery.service.Interface.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chatting Controller", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "채팅방 생성 API")
    @PostMapping("/mypage/room")
    public Long createRoom(@RequestBody RoomCreateDto roomCreateDto, HttpServletRequest request) {
        return chatService.createRoom(roomCreateDto, request);
    }

    @Operation(summary = "실시간 채팅 저장 API")
    @MessageMapping("/pub/chat")
    public void send(@RequestBody ChatMessageDto chatMessage) {
        chatService.saveChatMessage(chatMessage);
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    @Operation(summary = "내 채팅 목록 출력 API")
    @GetMapping("/mypage/room/{chatRoomId}")
    public List<ChatMessageDto> getLastChatMessages(@PathVariable Long chatRoomId, HttpServletRequest request) {
        List<ChatMessageDto> chatMessages = chatService.getLastChatMessages(chatRoomId, request);
        return chatMessages;
    }

    @Operation(summary = "내 채팅 목록 출력 API")
    @GetMapping("/mypage/chatList")
    public List<ChatRoomDto> getMyChatRoomsList(@RequestParam int page, HttpServletRequest request) {
        List<ChatRoomDto> chatRooms = chatService.getMyChatRoomsList(page, request);
        return chatRooms;
    }
}
