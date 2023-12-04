package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.calendar.CalendarOwnerResponseDto;
import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.dto.memo.MemoResponseDto;
import com.dessert.gallery.dto.schedule.ScheduleResponseDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.repository.CalendarRepository;
import com.dessert.gallery.service.Interface.CalendarService;
import com.dessert.gallery.service.Interface.MemoService;
import com.dessert.gallery.service.Interface.ScheduleService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.NOT_FOUND_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;
    private final ScheduleService scheduleService;
    private final MemoService memoService;
    private final UserService userService;

    @Override
    public void createCalendar(Store store) {
        Calendar calendar = new Calendar(store);
        calendarRepository.save(calendar);
    }

    @Override
    public CalendarResponseDto getCalendarByStore(Long storeId, int year, int month) {
        Calendar calendar = calendarRepository.findByStoreId(storeId);

        // 해당 월의 시작일 지정
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);

        // 해당 월의 마지막일 지정 + 윤년 계산을 위한 연산
        LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth()
                .length(startOfMonth.toLocalDate().isLeapYear()));
        List<Schedule> scheduleList = scheduleService.getSchedules(calendar, startOfMonth, endOfMonth);

        return toCalendarDto(year, month, scheduleList);
    }

    @Override
    public CalendarOwnerResponseDto getOwnerCalendar(int year, int month, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        if (user == null) throw new NotFoundException("존재하지 않는 유저", NOT_FOUND_EXCEPTION);
        Calendar calendar = calendarRepository.findByStore_User(user);
        // 해당 월의 시작일 지정
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);

        // 윤년 확인 + 해당 월의 마지막일 지정
        LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth()
                .length(startOfMonth.toLocalDate().isLeapYear()));
        List<Schedule> scheduleList = scheduleService.getSchedulesForOwner(calendar, startOfMonth, endOfMonth);
        List<Memo> memoList = memoService.getMemoList(calendar, startOfMonth, endOfMonth);

        return toOwnerCalendarDto(toCalendarDto(year, month, scheduleList), memoList);
    }

    private CalendarOwnerResponseDto toOwnerCalendarDto(CalendarResponseDto responseDto, List<Memo> memoList) {
        return new CalendarOwnerResponseDto(responseDto,
                memoList.stream().map(MemoResponseDto::new).collect(Collectors.toList()));
    }

    private CalendarResponseDto toCalendarDto(int year, int month, List<Schedule> scheduleList) {
        List<ScheduleResponseDto> scheduleResponseDtoList = scheduleList
                .stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());

        return new CalendarResponseDto(year, month, scheduleResponseDtoList);
    }
}
