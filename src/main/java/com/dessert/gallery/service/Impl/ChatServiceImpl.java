package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.dto.chat.list.RedisRecentChatDto;
import com.dessert.gallery.dto.chat.list.ChatRecentMessageDto;
import com.dessert.gallery.dto.chat.list.ChatRoomDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.*;
import com.dessert.gallery.service.Interface.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final RedisChatMessageCache messageMap;
    private static final int transactionMessageSize = 20; // 트랜잭션에 묶일 메세지 양
    private static final int messagePageableSize = 30; // roomId에 종속된 큐에 보관할 메세지의 양
    private final EntityManager em;

    @Override
    public Long createRoom(Long storeId, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));


        User customer = userRepository.findByEmail(email).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow();

        if (!subscribeRepository.existsByStoreAndUserAndDeletedIsFalse(store, customer)) {
            throw new UnAuthorizedException("Not Following", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (chatRoomRepository.existsByCustomerAndStore(customer, store)) {
            throw new UnAuthorizedException("This room has already been created.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .customer(customer)
                .store(store)
                .build();

        chatRoomRepository.save(chatRoom);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        RedisRecentChatDto dto = new RedisRecentChatDto(chatRoom.getId(), store.getId(), null, null, time);

        messageMap.putChatList(customer.getUid(), dto);

        return chatRoom.getId();
    }

    @Override
    public void saveMessage(Long id, MessageStatusDto messageStatusDto, HttpServletRequest request) {

        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Not Found Room", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        LocalDate currentDate = LocalDate.now();
        String time = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        RedisRecentChatDto redisRecentChatDto = new RedisRecentChatDto(messageStatusDto.getChatRoomId(),
                chatRoom.getStore().getId(),
                messageStatusDto.getMessage(),
                messageStatusDto.getMessageType(),
                messageStatusDto.getDateTime());

        if (!messageMap.containsKey(id, time)) {
            // 채팅방에 처음쓰는 글이라면 캐시가 없으므로 캐시를 생성
            Deque<MessageStatusDto> deque = new LinkedList<>();
            deque.add(messageStatusDto);

            messageMap.put(id, deque);
            messageMap.putChatList(chatRoom.getCustomer().getUid(), redisRecentChatDto);
        } else {
            Deque<MessageStatusDto> mDeque = messageMap.get(id, user.get().getUid(), time);
            mDeque.add(messageStatusDto);

            // 캐시 쓰기 전략 (Write Back 패턴)
            if (mDeque.size() > transactionMessageSize + messagePageableSize) {
                Deque<MessageStatusDto> q = new LinkedList<>();
                for (int i = 0; i < transactionMessageSize; i++) {
                    q.add(mDeque.poll());
                }

                commitMessageDeque(q, chatRoom); // commit
            }

            messageMap.put(id, mDeque);
            messageMap.putChatList(chatRoom.getCustomer().getUid(), redisRecentChatDto);
        }
    }

    @Override
    public ChatMessageDto getMessages(Long chatRoomId, String time, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        ChatMessageDto messageList = new ChatMessageDto();

        // 캐시 읽기 전략 (LookAside 패턴)
        if (!messageMap.containsKey(chatRoomId, time)) {
            // Cache Miss
            List<MessageStatusDto> messagesInDB = getMessagesInDB(chatRoomId, time);
            // DB 에도 없다면 새로 만든 방이므로 빈 리스트를 반환
            if (messagesInDB.isEmpty()) {
                return messageList;
            }

            // DB 에서 가져온 데이터를 Cache 에 적재
            Deque<MessageStatusDto> deque = new LinkedList<>(messagesInDB);

            assert deque.peek() != null;
            messageMap.put(chatRoomId, deque);

            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> {
                throw new NotFoundException("Not Found Room", ErrorCode.NOT_FOUND_EXCEPTION);
            });

            String uid;

            if (user.get().getUserRole().equals(UserRole.USER) || user.get().getUserRole().equals(UserRole.ADMIN)) {
                uid = chatRoom.getCustomer().getUid();
            } else {
                uid = chatRoom.getStore().getUser().getUid();
            }

            messageMap.putChatList(uid, new RedisRecentChatDto(deque.getLast().getChatRoomId(),
                    chatRoom.getStore().getId(),
                    deque.getLast().getMessage(),
                    deque.getLast().getMessageType(),
                    deque.getLast().getDateTime()));

            messageList.setChatList(messagesInDB);
        } else {
            // Cache Hit
            messageList.setChatList(getMessagesInCache(chatRoomId, user.get().getUid(), time));
        }

        // 현재 날짜 이후 가장 최근 채팅 일자를 불러온 뒤 싣는다.
        messageList.setLastDatetime(messageMap.getLastChatDateTime(chatRoomId, time));

        return messageList;
    }

    @Override
    public ChatRoomDto getMyChatRoomsList(int page, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        List<ChatRecentMessageDto> chatRecentMessageDtos = new ArrayList<>();
        List<RedisRecentChatDto> list = messageMap.getChatList(user.get().getUid());

        if (list == null || list.size() == 0) {
            return new ChatRoomDto(0, null);
        }

        for (int i = (page - 1) * 10; i < page * 10; i++) {
            if (list.size() <= i) {
                break;
            }

            ChatRecentMessageDto dto = ChatRecentMessageDto.builder()
                    .roomId(list.get(i).getRoomId())
                    .storeId(list.get(i).getStoreId())
                    .thumbnailMessage(list.get(i).getThumbnailMessage())
                    .messageType(list.get(i).getMessageType())
                    .lastChatDatetime(list.get(i).getDateTime())
                    .build();

            chatRecentMessageDtos.add(dto);
        }

        for (ChatRecentMessageDto chatRecentMessageDto : chatRecentMessageDtos) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRecentMessageDto.getRoomId()).orElseThrow(() -> {
                throw new NotFoundException("Not Found Room", ErrorCode.ACCESS_DENIED_EXCEPTION);
            });

            chatRecentMessageDto.setStoreName(chatRoom.getStore().getName());
            chatRecentMessageDto.setCustomerName(chatRoom.getCustomer().getNickname());
        }

        return ChatRoomDto.builder()
                .maxPage(list.size() / 10 + 1)
                .chatList(chatRecentMessageDtos)
                .build();
    }

    @Override
    public void deleteRoom(Long roomId, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() ->
                new UnAuthorizedException("Not Found Room", ErrorCode.ACCESS_DENIED_EXCEPTION));

        if (!chatRoom.getCustomer().getEmail().equals(email) && !chatRoom.getStore().getUser().getEmail().equals(email)) {
            throw new UnAuthorizedException("Unauthorized User", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        chatRoomRepository.delete(chatRoom);
        messageMap.deleteChatRoom(roomId, user.get().getUid()); // Redis 내 관련 정보 삭제
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredChatMessages() {
        messageMap.deleteOldMessages();
    }

    private List<MessageStatusDto> getMessagesInDB(Long roomId, String time) {
        String start = time + " " + "00:00:00"; // yyyy-MM-dd 00:00:00
        String end = time + " " + "23:59:59"; // yyyy-MM-dd 23:59:59

        return chatMessageRepository.findByChatRoomIdAndDateTimeBetweenOrderByDateTimeDesc(roomId, start, end);
    }

    private List<MessageStatusDto> getMessagesInCache(Long roomId, String uid, String time) {
        return messageMap.get(roomId, uid, time);
    }

    private void commitMessageDeque(Deque<MessageStatusDto> messageDeque, ChatRoom chatRoom) {

        // 쓰기 지연
        for (int i = 0; i < messageDeque.size(); i++) {
            MessageStatusDto dto = Objects.requireNonNull(messageDeque.poll());
            ChatMessage message = new ChatMessage(chatRoom, dto.getDateTime(), dto);

            em.persist(message);
        }

        em.flush();
    }
}
