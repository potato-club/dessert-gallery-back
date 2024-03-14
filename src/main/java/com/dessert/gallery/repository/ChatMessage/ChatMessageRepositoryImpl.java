package com.dessert.gallery.repository.ChatMessage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {

    private final ChatMessageRepository chatMessageRepository;
    private final JPAQueryFactory jpaQueryFactory;

}
