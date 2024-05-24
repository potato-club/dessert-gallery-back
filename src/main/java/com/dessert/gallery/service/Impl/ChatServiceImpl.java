package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.chat.ChatMessageDto;
import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.dto.chat.list.ChatRecentMessageDto;
import com.dessert.gallery.dto.chat.list.ChatRoomDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.*;
import com.dessert.gallery.repository.ChatMessage.ChatMessageRepository;
import com.dessert.gallery.repository.ChatRoom.ChatRoomRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.repository.Subscribe.SubscribeRepository;
import com.dessert.gallery.repository.User.UserRepository;
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
    private final EntityManager em;

    @Override
    @Transactional
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
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }

    @Override
    @Transactional
    public String saveMessage(Long id, MessageStatusDto messageStatusDto) {

        Optional<User> user = userRepository.findByNickname(messageStatusDto.getSender());

        if (user.isEmpty()) {
            return "Not Found User";
        }

        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow();

        if (user.get().getUserRole().equals(UserRole.USER)) {
            if (!subscribeRepository.existsByStoreAndUserAndDeletedIsFalse(chatRoom.getStore(), user.get())) {
                return "Not Following";
            }
        } else if (user.get().getUserRole().equals(UserRole.MANAGER)) {
            if (!subscribeRepository.existsFollowingManager(chatRoom.getStore())) {
                return "Not Following";
            }
        }

        LocalDate currentDate = LocalDate.now();
        String time = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (messageMap.isContainsKey(id, time)) {
            // 오늘 채팅방에 처음 쓰는 글이라면 캐시가 없으므로 캐시를 생성
            Deque<MessageStatusDto> deque = new LinkedList<>();
            deque.add(messageStatusDto);

            messageMap.put(id, deque);
        } else {
            Deque<MessageStatusDto> deque = messageMap.get(id, time);
            deque.add(messageStatusDto);

            messageMap.put(id, deque);

            // 캐시 쓰기 전략 (Write Back 패턴)
            if (deque.size() % transactionMessageSize == 0 && deque.size() >= transactionMessageSize) {
                Deque<MessageStatusDto> dq = new LinkedList<>();

                for (int i = 0; i < transactionMessageSize; i++) {
                    dq.add(deque.pollLast());
                }

                commitMessageDeque(dq, chatRoom); // commit
            }
        }

        chatRoom.updateDateTime(LocalDateTime.now());   // 채팅방 리스트를 가져올 때 최신 채팅 기준으로 정렬하기 위해 시간 최신화
        return "ok";
    }

    @Override
    public ChatMessageDto getMessages(Long chatRoomId, String time, HttpServletRequest request) {

        ChatMessageDto messageList = new ChatMessageDto();

        // 캐시 읽기 전략 (LookAside 패턴)
        if (messageMap.isContainsKey(chatRoomId, time)) {
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

            messageList.setChatList(messagesInDB);
        } else {
            // Cache Hit
            messageList.setChatList(getMessagesInCache(chatRoomId, time));
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

        List<ChatRoom> list = chatRoomRepository.getChatRoomsList(page, user.get());

        return this.commonComponent(list);
    }

    @Override
    public ChatRoomDto searchChatRoom(int page, String name, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        List<ChatRoom> list = chatRoomRepository.searchChatRoomsList(page, user.get().getUserRole(), user.get(), name);

        return this.commonComponent(list);
    }


    @Override
    @Transactional
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
        messageMap.deleteChatRoom(roomId); // Redis 내 관련 정보 삭제
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

    private List<MessageStatusDto> getMessagesInCache(Long roomId, String time) {
        return messageMap.get(roomId, time);
    }

    private void commitMessageDeque(Deque<MessageStatusDto> messageDeque, ChatRoom chatRoom) {

        // 쓰기 지연
        while (!messageDeque.isEmpty()) {
            MessageStatusDto dto = messageDeque.pollLast();

            assert dto != null;
            ChatMessage message = new ChatMessage(chatRoom, dto.getDateTime(), dto);

            em.persist(message);
        }

        em.flush();
    }

    // 채팅방 내역 출력 및 검색 서비스 공통 컴포넌트
    private ChatRoomDto commonComponent(List<ChatRoom> list) {

        List<ChatRecentMessageDto> chatRecentMessageDtos = new ArrayList<>();

        // 채팅방 내역이 없으면 빈 리스트 반환
        if (list == null || list.size() == 0) {
            return new ChatRoomDto(0, null);
        }

        // 썸네일 메시지와 메시지 타입, 최근 채팅 시간 등을 싣는다.
        for (ChatRoom chatRoom : list) {
            MessageStatusDto messageStatusDto = messageMap.getRecentChatData(chatRoom.getId());

            ChatRecentMessageDto chatRecentMessageDto = ChatRecentMessageDto.builder()
                    .roomId(chatRoom.getId())
                    .storeId(chatRoom.getStore().getId())
                    .storeName(chatRoom.getStore().getName())
                    .customerName(chatRoom.getCustomer().getNickname())
                    .thumbnailMessage(messageStatusDto.getMessage())
                    .messageType(messageStatusDto.getMessageType())
                    .lastChatDatetime(messageStatusDto.getDateTime())
                    .build();

            chatRecentMessageDtos.add(chatRecentMessageDto);
        }

        return ChatRoomDto.builder()
                .maxPage(list.size() / 10 + 1)
                .chatList(chatRecentMessageDtos)
                .build();
    }
}
