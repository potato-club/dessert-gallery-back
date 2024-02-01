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

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

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
    private final RedisChatMessageCache messageMap;
    private static final int transactionMessageSize = 20; // 트랜잭션에 묶일 메세지 양
    private static final int messagePageableSize = 30; // roomId에 종속된 큐에 보관할 메세지의 양
    private final EntityManager em;


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
                .store(store)
                .build();

        ChatRoom createRoom = chatRoomRepository.save(chatRoom);
        return createRoom.getId();
    }

    @Override
    public void saveMessage(Long id, ChatMessageDto chatMessageDto) {

        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not Found Room", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        if (!messageMap.containsKey(id)) {
            // 채팅방에 처음쓰는 글이라면 캐시가 없으므로 캐시를 생성
            Queue<ChatMessageDto> queue = new LinkedList<>();
            queue.add(chatMessageDto);
            messageMap.put(id, queue);
        } else {
            Queue<ChatMessageDto> mQueue = messageMap.get(id);
            mQueue.add(chatMessageDto);

            // 캐시 쓰기 전략 (Write Back 패턴)
            if (mQueue.size() > transactionMessageSize + messagePageableSize) {
                Queue<ChatMessageDto> q = new LinkedList<>();
                for (int i = 0; i < transactionMessageSize; i++) {
                    q.add(mQueue.poll());
                }

                commitMessageQueue(q, chatRoom); // commit
            }

            messageMap.put(id, mQueue);
        }
    }

    @Override
    public List<ChatMessageDto> getMessages(Long chatRoomId, HttpServletRequest request) {
        // 캐시 읽기 전략 (LookAside 패턴)
        List<ChatMessageDto> messageList = new ArrayList<>();

        if (!messageMap.containsKey(chatRoomId)) {
            // Cache Miss
            List<ChatMessageDto> messagesInDB = getMessagesInDB(chatRoomId);
            // DB 에도 없다면 새로 만든 방이므로 빈 리스트를 반환
            if (messagesInDB.isEmpty()) {
                return messageList;
            }

            // DB 에서 가져온 데이터를 Cache 에 적재
            Queue<ChatMessageDto> queue = new LinkedList<>(messagesInDB);
            messageMap.put(chatRoomId, queue);
            messageList = messagesInDB;
        } else {
            // Cache Hit
            messageList = getMessagesInCache(chatRoomId);
        }

        return messageList;
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
                .where(qChatRoom.customer.uid.eq(user.get().getUid()).or(qChatRoom.store.user.uid.eq(user.get().getUid())))
                .distinct()
                .orderBy(qChatMessage.localDateTime.desc())
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

        if (!chatRoom.getCustomer().getEmail().equals(email) && !chatRoom.getStore().getUser().getEmail().equals(email)) {
            throw new UnAuthorizedException("Unauthorized User", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        chatMessageRepository.deleteByChatRoomId(chatRoom.getId());
        chatRoomRepository.delete(chatRoom);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredChatMessages() {
        messageMap.deleteOldMessages();
    }

    private List<ChatMessageDto> getMessagesInDB(Long roomId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return chatMessageRepository.findByChatRoomIdAndLocalDateTimeAfterOrderByLocalDateTimeDesc(roomId, oneMonthAgo);
    }

    private List<ChatMessageDto> getMessagesInCache(Long roomId) {
        return messageMap.get(roomId);
    }

    private void commitMessageQueue(Queue<ChatMessageDto> messageQueue, ChatRoom chatRoom) {

        // 쓰기 지연
        for (int i = 0; i < messageQueue.size(); i++) {
            ChatMessage message = new ChatMessage(chatRoom, LocalDateTime.now(), Objects.requireNonNull(messageQueue.poll()));
            em.persist(message);
        }

        em.flush();
    }
}
