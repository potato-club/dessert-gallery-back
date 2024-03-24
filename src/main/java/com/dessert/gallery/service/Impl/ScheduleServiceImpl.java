package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.chat.MessageStatusDto;
import com.dessert.gallery.dto.schedule.*;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.ScheduleType;
import com.dessert.gallery.error.exception.BadRequestException;
import com.dessert.gallery.error.exception.DuplicateException;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.ChatRoom.ChatRoomRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.service.Interface.ChatService;
import com.dessert.gallery.repository.Calendar.CalendarRepository;
import com.dessert.gallery.repository.Schedule.ScheduleRepository;
import com.dessert.gallery.repository.User.UserRepository;
import com.dessert.gallery.service.Interface.ScheduleService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dessert.gallery.enums.MessageType.REVIEW;
import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final StoreRepository storeRepository;
    private final CalendarRepository calendarRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final UserService userService;

    @Override
    public void addSchedule(ScheduleRequestDto requestDto, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(user);

        LocalDateTime dateTime = LocalDate.parse(requestDto.getDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();

        boolean isHoliday = scheduleRepository
                .existsByCalendarAndDateTimeAndType(calendar, dateTime, ScheduleType.HOLIDAY);

        boolean isEvent = scheduleRepository
                .existsByCalendarAndDateTimeAndType(calendar, dateTime, ScheduleType.EVENT);

        if (requestDto.getKey() != 2 && requestDto.getKey() != 3) {
            throw new BadRequestException("잘못된 스케줄 타입 입력", BAD_REQUEST_EXCEPTION);
        }

        if (isHoliday && requestDto.getKey() == 2) {
            throw new DuplicateException("스케줄 등록 중복", CONFLICT_EXCEPTION);
        }

        if (isEvent && requestDto.getKey() == 3) {
            throw new DuplicateException("스케줄 등록 중복", CONFLICT_EXCEPTION);
        }

        Schedule schedule = new Schedule(requestDto, calendar);
        Schedule saveSchedule = scheduleRepository.save(schedule);
        calendar.addSchedule(saveSchedule);
    }

    @Override
    public void addReservation(ReservationRequestDto requestDto, HttpServletRequest request) {
        User owner = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(owner);

        User client = userRepository.findByNickname(requestDto.getClient())
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않는 client nickname", NOT_FOUND_EXCEPTION);
                });

        Schedule schedule = new Schedule(requestDto, client, calendar);
        Schedule saveSchedule = scheduleRepository.save(schedule);
        calendar.addSchedule(saveSchedule);
    }

    @Override
    public void toggleSchedule(Long scheduleId, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(user);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다.", NOT_FOUND_EXCEPTION));

        if (schedule.getCalendar().getStore().getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }

        if (schedule.getType() != ScheduleType.RESERVATION) {
            throw new BadRequestException("예약 스케줄만 체크할 수 있습니다.", BAD_REQUEST_EXCEPTION);
        }

        if (schedule.getSubmitReview()) {
            throw new BadRequestException("이미 리뷰 등록된 스케줄입니다.", SCHEDULE_EXCEPTION_1);
        }

        Schedule newSchedule = schedule.toggleSchedule();
        calendar.removeSchedule(schedule);
        calendar.addSchedule(newSchedule);

        // 픽업 완료 시 30분 뒤에 채팅방에 메시지 전송 메서드 스케줄링
        if (newSchedule.getCompleted()) {
            ChatRoom chatRoom = chatRoomRepository.findByStore_UserAndCustomer(user, schedule.getClient());
            sendMessageScheduling(chatRoom, scheduleId);
        }
    }

    private void sendMessageScheduling(ChatRoom chatRoom, Long scheduleId) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            sendMessage(chatRoom, scheduleId);
        }, 1, TimeUnit.MINUTES);
        scheduler.shutdown();
    }

    private void sendMessage(ChatRoom room, Long scheduleId) {
        // 만약 잘못 체크해서 30분내에 픽업 체크를 해제했다면 saveMessage를 호출하지 못하게 함
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다.", NOT_FOUND_EXCEPTION));
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (schedule.getCompleted()) {
            // 채팅 메시지 어떻게 보낼건지??
            MessageStatusDto message = MessageStatusDto.builder()
                    .message("1분 뒤 test message 저장되나?")
                    .sender("park")
                    .messageType(REVIEW)
                    .dateTime(now)
                    .build();

            chatService.saveMessage(room.getId(), message);
        }
    }

    @Override
    public void removeSchedule(Long scheduleId, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다.", NOT_FOUND_EXCEPTION));

        LocalDateTime reviewWritableMaxDate = LocalDateTime.now().minusDays(7);

        if (schedule.getCalendar().getStore().getUser() != user)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        // 예약 스케줄이면서 리뷰 등록이 안됐다면 픽업 완료 처리 + 수정 날짜가 7일이 안넘었을 경우 삭제 불가
        if (schedule.getType() == ScheduleType.RESERVATION && !schedule.getSubmitReview()) {
            if (schedule.getCompleted() &&
                    schedule.getModifiedDate().isAfter(reviewWritableMaxDate)) {
                throw new BadRequestException("픽업 완료 된 예약 스케줄은 완료 처리 7일 후 삭제 가능", SCHEDULE_EXCEPTION_2);
            }
        }

        schedule.removeSchedule();
        scheduleRepository.delete(schedule);
    }

    @Override
    public List<Schedule> getSchedules(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleRepository.
                findByCalendarAndDateTimeBetweenAndTypeIsNot(calendar, startDate, endDate, ScheduleType.RESERVATION);
    }

    @Override
    public List<Schedule> getSchedulesForOwner(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleRepository.
                findByCalendarAndDateTimeBetween(calendar, startDate, endDate);
    }

    @Override
    public List<ReservationResponseForChat> getReservationsForChat(Long storeId, String nickname, HttpServletRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("해당 가게를 찾을 수 없습니다.", NOT_FOUND_EXCEPTION));

        User owner = userService.findUserByToken(request);

        if (store.getUser() != owner) {
            throw new UnAuthorizedException("요청에 대한 권한이 없습니다.", ACCESS_DENIED_EXCEPTION);
        }

        User client = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다.", NOT_FOUND_EXCEPTION));

        List<Schedule> reservationList = scheduleRepository
                .findAllByCalendar_StoreAndClientAndCompletedIsFalse(store, client);

        return reservationList.stream()
                .map(ReservationResponseForChat::new)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDetailResponseDto getDetailScheduleByDate(int year, int month, int day, HttpServletRequest request) {
        User owner = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(owner);

        LocalDateTime startOfDay = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.of(year, month, day),
                LocalTime.of(23, 59, 59));

        List<Schedule> scheduleList = scheduleRepository
                .findByCalendarAndDateTimeBetween(calendar, startOfDay, endOfDay);

        List<ReservationResponseDto> reservationList = new ArrayList<>();
        Long holidayId = null;
        Long eventId = null;

        for (Schedule schedule : scheduleList) {
            if (schedule.getType() == ScheduleType.RESERVATION) {
                reservationList.add(new ReservationResponseDto(schedule, schedule.getClient().getNickname()));
            }

            if (schedule.getType() == ScheduleType.HOLIDAY) {
                holidayId = schedule.getId();
            }

            if (schedule.getType() == ScheduleType.EVENT) {
                eventId = schedule.getId();
            }
        }

        Collections.sort(reservationList);

        return new ScheduleDetailResponseDto(startOfDay, reservationList, holidayId, eventId);
    }

    @Override
    public boolean getTodayIsHoliday(HttpServletRequest request, LocalDateTime today) {
        User owner = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(owner);

        return scheduleRepository.existsByCalendarAndDateTimeAndType(calendar, today, ScheduleType.HOLIDAY);
    }
}
