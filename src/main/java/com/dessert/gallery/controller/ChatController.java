package com.dessert.gallery.controller;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.dto.chat.list.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;
import com.dessert.gallery.repository.RedisChatMessageCache;
import com.dessert.gallery.service.Interface.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chatting Controller", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisChatMessageCache redisChatMessageCache;

    @Operation(summary = "채팅방 생성 API")
    @PostMapping("/mypage/room/{storeId}")
    public Long createRoom(@PathVariable Long storeId, HttpServletRequest request) {
        return chatService.createRoom(storeId, request);
    }

    @Operation(summary = "실시간 채팅 저장 API")
    @MessageMapping("/chat")
    public void send(MessageStatusDto messageStatusDto) {
        chatService.saveMessage(messageStatusDto.getChatRoomId(), messageStatusDto);
        messagingTemplate.convertAndSend("/sub/" + messageStatusDto.getChatRoomId(), messageStatusDto);
    }

    @Operation(summary = "내 채팅 내역 출력 API")
    @GetMapping("/mypage/room/{roomId}")
    public ChatMessageDto getLastChatMessages(@PathVariable Long roomId, @RequestParam String time, HttpServletRequest request) {
        return chatService.getMessages(roomId, time, request);
    }

    @Operation(summary = "내 채팅방 목록 출력 API")
    @GetMapping("/mypage/room")
    public ChatRoomDto getMyChatRoomsList(@RequestParam int page, HttpServletRequest request) {
        return chatService.getMyChatRoomsList(page, request);
    }

    @Operation(summary = "채팅방 나가기 API")
    @DeleteMapping("/mypage/room/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId, HttpServletRequest request) {
        chatService.deleteRoom(roomId, request);
        return ResponseEntity.ok("채팅방에서 나가셨습니다.");
    }

    @Operation(summary = "Redis 데이터 초기화 API")
    @DeleteMapping("/redis")
    public ResponseEntity<String> deleteCache() {
        redisChatMessageCache.deleteAll();
        return ResponseEntity.ok("Redis 캐쉬 삭제가 완료되었습니다.");
    }
}
