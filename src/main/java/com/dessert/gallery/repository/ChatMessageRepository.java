package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessageDto> findByChatRoomIdAndLocalDateTimeAfterOrderByLocalDateTimeDesc(Long chatRoomId, LocalDateTime localDateTime);

    void deleteByChatRoomId(Long roomId);
}
