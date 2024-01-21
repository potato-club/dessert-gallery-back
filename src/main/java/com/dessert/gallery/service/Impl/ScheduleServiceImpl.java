package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.schedule.ReservationRequestDto;
import com.dessert.gallery.dto.schedule.ReservationResponseDto;
import com.dessert.gallery.dto.schedule.ScheduleDetailResponseDto;
import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.ScheduleType;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.BadRequestException;
import com.dessert.gallery.error.exception.DuplicateException;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.CalendarRepository;
import com.dessert.gallery.repository.ScheduleRepository;
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

import static com.dessert.gallery.error.ErrorCode.NOT_ALLOW_WRITE_EXCEPTION;
import static com.dessert.gallery.error.ErrorCode.NOT_FOUND_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final CalendarRepository calendarRepository;
    private final ScheduleRepository scheduleRepository;
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
            throw new BadRequestException("잘못된 스케줄 타입 입력", ErrorCode.RUNTIME_EXCEPTION);
        }

        if (isHoliday && requestDto.getKey() == 2) {
            throw new DuplicateException("스케줄 등록 중복", ErrorCode.CONFLICT_EXCEPTION);
        }

        if (isEvent && requestDto.getKey() == 3) {
            throw new DuplicateException("스케줄 등록 중복", ErrorCode.CONFLICT_EXCEPTION);
        }

        Schedule schedule = new Schedule(requestDto, calendar);
        Schedule saveSchedule = scheduleRepository.save(schedule);
        calendar.addSchedule(saveSchedule);
    }

    @Override
    public void addReservation(ReservationRequestDto requestDto, HttpServletRequest request) {
        User owner = userService.findUserByToken(request);
        Calendar calendar = calendarRepository.findByStore_User(owner);

        Schedule schedule = new Schedule(requestDto, calendar);
        Schedule saveSchedule = scheduleRepository.save(schedule);
        calendar.addSchedule(saveSchedule);
    }

    @Override
    public void removeSchedule(Long scheduleId, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다.", NOT_FOUND_EXCEPTION));
        if (schedule.getCalendar().getStore().getUser() != user)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
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
                reservationList.add(new ReservationResponseDto(schedule));
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
