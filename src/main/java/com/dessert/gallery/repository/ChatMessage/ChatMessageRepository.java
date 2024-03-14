package com.dessert.gallery.repository.ChatMessage;

import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<MessageStatusDto> findByChatRoomIdAndDateTimeBetweenOrderByDateTimeDesc(Long chatRoomId, String start, String end);
}
