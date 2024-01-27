package com.dessert.gallery.controller;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;
import com.dessert.gallery.entity.ChatMessage;
import com.dessert.gallery.service.Interface.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    @MessageMapping("/chat")
    public void send(@RequestBody ChatMessageDto chatMessage) {
        chatService.saveChatMessage(chatMessage.getId(), chatMessage);
        messagingTemplate.convertAndSend("/sub/" + chatMessage.getId(), chatMessage);
    }

    @Operation(summary = "내 채팅 내역 출력 API")
    @GetMapping("/mypage/room/{roomId}")
    public List<ChatMessageDto> getLastChatMessages(@PathVariable Long roomId, HttpServletRequest request) {
        return chatService.getLastChatMessages(roomId, request);
    }

    @Operation(summary = "내 채팅방 목록 출력 API")
    @GetMapping("/mypage/room")
    public List<ChatRoomDto> getMyChatRoomsList(@RequestParam int page, HttpServletRequest request) {
        return chatService.getMyChatRoomsList(page, request);
    }

    @Operation(summary = "채팅방 나가기 API")
    @DeleteMapping("/mypage/room/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId, HttpServletRequest request) {
        chatService.deleteRoom(roomId, request);
        return ResponseEntity.ok("채팅방에서 나가셨습니다.");
    }
}
