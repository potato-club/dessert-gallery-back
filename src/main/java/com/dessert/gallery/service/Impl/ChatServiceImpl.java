package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.ChatMessageRepository;
import com.dessert.gallery.repository.ChatRoomRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.ChatService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long createRoom(RoomCreateDto roomCreateDto, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User customer = userRepository.findByEmail(email).orElseThrow();
        Store store = storeRepository.findById(roomCreateDto.getStoreId()).orElseThrow();

        ChatRoom chatRoom = ChatRoom.builder()
                .customer(customer)
                .owner(store.getUser())
                .store(store)
                .build();

        ChatRoom createRoom = chatRoomRepository.save(chatRoom);
        return createRoom.getId();
    }

    @Override
    public void saveChatMessage(ChatMessageDto chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage.toEntity());
    }

    @Override
    public List<ChatMessageDto> getLastChatMessages(Long chatRoomId, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> {
           throw new UnAuthorizedException("401", ErrorCode.ACCESS_DENIED_EXCEPTION);
        });

//        if (chatRoom.getCustomer().getEmail().equals(email)) {
//
//        }

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return chatMessageRepository.findByChatRoomIdAndTimestampAfterOrderByTimestamp(chatRoomId, oneMonthAgo);
    }

    @Override
    public List<ChatRoomDto> getMyChatRoomsList(int page, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);
        QChatRoom qChatRoom = QChatRoom.chatRoom;
        QChatMessage qChatMessage = QChatMessage.chatMessage;

        JPAQuery<ChatRoomDto> query = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ChatRoomDto.class,
                                qChatRoom.id.as("roomId"),
                                qChatRoom.store.name.as("storeName"),
                                qChatRoom.customer.nickname.as("customerName"),
                                qChatMessage.message.as("thumbnailMessage")
                        )
                )
                .from(qChatRoom)
                .leftJoin(qChatMessage).on(qChatMessage.chatRoom.id.eq(qChatRoom.id))
                .where(qChatRoom.customer.uid.eq(user.get().getUid()).or(qChatRoom.owner.uid.eq(user.get().getUid())))
                .distinct()
                .orderBy(qChatMessage.timestamp.desc())
                .offset((page - 1) * 10L)
                .limit(10);

        return query.fetch();
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredChatMessages() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        chatMessageRepository.deleteByTimestampBefore(oneMonthAgo);
    }
}
