package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface StoreService {
    void createStore(StoreRequestDto requestDto, HttpServletRequest request);
    StoreResponseDto getStore(Long id);
    void updateStore(Long id, StoreRequestDto updateDto, HttpServletRequest request);
    void removeStore(Long id, HttpServletRequest request);
}
