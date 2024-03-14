package com.dessert.gallery.repository.ChatRoom;

import com.dessert.gallery.entity.ChatRoom;
import com.dessert.gallery.entity.QChatRoom;
import com.dessert.gallery.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatRoom> getChatRoomsList(int page, User user) {
        return jpaQueryFactory
                .select(QChatRoom.chatRoom)
                .from(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.customer.eq(user)
                        .or(QChatRoom.chatRoom.store.user.eq(user)))
                .orderBy(QChatRoom.chatRoom.modifiedDate.desc())
                .offset((page - 1) * 10L)
                .limit(10)
                .fetch();
    }
}
