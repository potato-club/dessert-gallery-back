package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.dto.schedule.ScheduleResponseDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.repository.CalendarRepository;
import com.dessert.gallery.service.Interface.CalendarService;
import com.dessert.gallery.service.Interface.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;
    private final ScheduleService scheduleService;

    @Override
    public void createCalendar(Store store) {
        Calendar calendar = new Calendar(store);
        calendarRepository.save(calendar);
    }

    @Override
    public void removeCalendar(Long storeId) {
        Calendar calendar = findCalendar(storeId);
        calendarRepository.delete(calendar);
    }

    @Override
    public Calendar findCalendar(Long storeId) {
        return calendarRepository.findByStoreId(storeId);
    }

    @Override
    public CalendarResponseDto getCalendarByStore(Long storeId, int year, int month) {
        Calendar calendar = findCalendar(storeId);

        // 해당 월의 시작일 지정
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);

        // 해당 월의 마지막일 지정 + 윤년 계산을 위한 연산
        LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth()
                .length(startOfMonth.toLocalDate().isLeapYear()));
        List<Schedule> scheduleList = scheduleService.getSchedules(calendar, startOfMonth, endOfMonth);

        return toCalendarDto(year, month, scheduleList);
    }

    @Override
    public CalendarResponseDto getOwnerCalendar(Long storeId, int year, int month, HttpServletRequest request) {
        Calendar calendar = findCalendar(storeId);

        // 해당 월의 시작일 지정
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);

        // 윤년 확인 + 해당 월의 마지막일 지정
        LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.getMonth()
                .length(startOfMonth.toLocalDate().isLeapYear()));
        List<Schedule> scheduleList = scheduleService.getSchedulesForOwner(calendar, startOfMonth, endOfMonth);

        return toCalendarDto(year, month, scheduleList);
    }

    private static CalendarResponseDto toCalendarDto(int year, int month, List<Schedule> scheduleList) {
        List<ScheduleResponseDto> scheduleResponseDtoList = scheduleList
                .stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());

        return new CalendarResponseDto(year, month, scheduleResponseDtoList);
    }
}
