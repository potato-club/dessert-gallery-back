package com.dessert.gallery.service.Interface;


import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    void addSchedule(Long storeId, ScheduleRequestDto requestDto, HttpServletRequest request);
    void removeSchedule(Long scheduleId, HttpServletRequest request);
    List<Schedule> getSchedules(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate);
    List<Schedule> getSchedulesForOwner(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate);
}
