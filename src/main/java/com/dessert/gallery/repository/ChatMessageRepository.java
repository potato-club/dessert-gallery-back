package com.dessert.gallery.repository;

import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<MessageStatusDto> findByChatRoomIdAndDateTimeBetweenOrderByDateTimeDesc(Long chatRoomId, String start, String end);

    void deleteByChatRoomId(Long roomId);
}
