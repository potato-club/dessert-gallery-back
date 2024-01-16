package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.ChatRoomDto;
import com.dessert.gallery.dto.chat.RoomCreateDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.*;
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
import java.time.ZoneId;
import java.util.LinkedList;
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
    private final SubscribeRepository subscribeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long createRoom(RoomCreateDto roomCreateDto, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User customer = userRepository.findByEmail(email).orElseThrow();
        Store store = storeRepository.findById(roomCreateDto.getStoreId()).orElseThrow();

        if (!subscribeRepository.existsByStoreAndUserAndDeletedIsFalse(store, customer)) {
            throw new UnAuthorizedException("Not Following", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .customer(customer)
                .owner(store.getUser())
                .store(store)
                .build();

        ChatRoom createRoom = chatRoomRepository.save(chatRoom);
        return createRoom.getId();
    }

    @Override
    public void saveChatMessage(Long id, ChatMessageDto chatMessage) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not Found Room", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        chatMessage.setChatRoom(chatRoom);
        chatMessageRepository.save(chatMessage.toEntity());
    }

    @Override
    public List<ChatMessageDto> getLastChatMessages(Long chatRoomId, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> {
           throw new UnAuthorizedException("401", ErrorCode.ACCESS_DENIED_EXCEPTION);
        });

        User owner = chatRoom.getOwner();
        User customer = chatRoom.getCustomer();

        // 요청하는 유저가 해당 채팅방에 참여하고 있는지 확인함.
        if (!owner.getEmail().equals(email) && !customer.getEmail().equals(email)) {
            throw new UnAuthorizedException("Unauthorized User", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return chatMessageRepository.findByChatRoomIdAndTimestampAfterOrderByTimestampDesc(chatRoomId, oneMonthAgo);
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
                                qChatMessage.message.as("thumbnailMessage"),
                                qChatMessage.messageType.as("messageType")
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
    public void deleteRoom(Long roomId, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UnAuthorizedException("Unauthorized User", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() ->
                new UnAuthorizedException("Not Found User", ErrorCode.ACCESS_DENIED_EXCEPTION));

        if (!chatRoom.getCustomer().getEmail().equals(email) && !chatRoom.getOwner().getEmail().equals(email)) {
            throw new UnAuthorizedException("Unauthorized User", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        chatMessageRepository.deleteByChatRoomId(chatRoom.getId());
        chatRoomRepository.delete(chatRoom);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredChatMessages() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        long milliseconds = oneMonthAgo.atZone(zoneId).toInstant().toEpochMilli();

        chatMessageRepository.deleteByTimestampBefore(oneMonthAgo);
    }
}
