package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Store;

import javax.servlet.http.HttpServletRequest;

public interface CalendarService {
    void createCalendar(Store store);
    void removeCalendar(Long storeId);
    Calendar findCalendar(Long storeId);
    CalendarResponseDto getCalendarByStore(Long storeId, int year, int month);
    CalendarResponseDto getOwnerCalendar(Long storeId, int year, int month, HttpServletRequest request);
}
