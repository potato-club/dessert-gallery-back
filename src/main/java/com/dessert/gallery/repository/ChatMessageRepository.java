package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessageDto> findByChatRoomIdAndTimestampAfterOrderByTimestamp(Long chatRoomId, LocalDateTime timestamp);

    void deleteByTimestampBefore(LocalDateTime timestamp);

    void deleteByChatRoomId(Long roomId);
}
