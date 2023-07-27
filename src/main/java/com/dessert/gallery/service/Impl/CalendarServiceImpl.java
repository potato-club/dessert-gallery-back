package com.dessert.gallery.service.Impl;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final StoreRepository storeRepository;
    @Override
    public void getCalendarByStore(Long storeId, int year, int month) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("store is not exist", ErrorCode.NOT_FOUND_EXCEPTION));

    }

    @Override
    public void getOwnerCalendar(Long storeId, int year, int month, HttpServletRequest request) {

    }
}
