package com.dessert.gallery.service.Interface;

import javax.servlet.http.HttpServletRequest;

public interface CalendarService {
    void getCalendarByStore(Long storeId, int year, int month);
    void getOwnerCalendar(Long storeId, int year, int month, HttpServletRequest request);
}
