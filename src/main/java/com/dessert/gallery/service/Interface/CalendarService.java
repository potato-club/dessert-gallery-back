package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.calendar.CalendarOwnerResponseDto;
import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.entity.Store;

import javax.servlet.http.HttpServletRequest;

public interface CalendarService {
    void createCalendar(Store store);
    CalendarResponseDto getCalendarByStore(Long storeId, int year, int month);
    CalendarOwnerResponseDto getOwnerCalendar(int year, int month, HttpServletRequest request);
}
