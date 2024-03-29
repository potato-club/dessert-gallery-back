package com.dessert.gallery.entity;

import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.enums.MessageType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Column
    private String sender;

    @Column
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column
    private String dateTime;

    @Builder
    public ChatMessage(ChatRoom chatRoom, String dateTime, MessageStatusDto dto) {
        this.sender = dto.getSender();
        this.chatRoom = chatRoom;
        this.message = dto.getMessage();
        this.messageType = dto.getMessageType();
        this.dateTime = dateTime;
    }
}
