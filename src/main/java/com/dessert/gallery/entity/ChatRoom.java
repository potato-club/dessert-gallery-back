package com.dessert.gallery.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_uid")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public ChatRoom(Long id, User customer, Store store, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.customer = customer;
        this.store = store;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public void updateDateTime(LocalDateTime dateTime) {
        this.modifiedDate = dateTime;
    }
}
