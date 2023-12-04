package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.ScheduleType;
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

import java.time.LocalDateTime;
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
                findByCalendarAndDateTimeBetweenAndType(calendar, startDate, endDate, ScheduleType.HOLIDAY);
    }

    @Override
    public List<Schedule> getSchedulesForOwner(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleRepository.
                findByCalendarAndDateTimeBetween(calendar, startDate, endDate);
    }
}
