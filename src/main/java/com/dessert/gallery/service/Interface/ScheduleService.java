package com.dessert.gallery.service.Interface;


import com.dessert.gallery.dto.schedule.*;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    void addSchedule(ScheduleRequestDto requestDto, HttpServletRequest request);
    void addReservation(ReservationRequestDto requestDto, HttpServletRequest request);
    void toggleSchedule(Long scheduleId, HttpServletRequest request);
    void removeSchedule(Long scheduleId, HttpServletRequest request);
    List<Schedule> getSchedules(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate);
    List<Schedule> getSchedulesForOwner(Calendar calendar, LocalDateTime startDate, LocalDateTime endDate);
    List<ReservationResponseForChat> getReservationsForChat(Long storeId, String nickname, HttpServletRequest request);
    ScheduleDetailResponseDto getDetailScheduleByDate(int year, int month, int day, HttpServletRequest request);
    boolean getTodayIsHoliday(HttpServletRequest request, LocalDateTime today);
}
