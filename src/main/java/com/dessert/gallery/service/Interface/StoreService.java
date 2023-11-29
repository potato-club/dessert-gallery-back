package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.store.StoreOwnerResponseDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface StoreService {
    Store getStore(Long id);
    Store getStoreByUser(User user);
    StoreOwnerResponseDto getStoreDtoByUser(HttpServletRequest request);
    StoreResponseDto getStoreDto(Long id, HttpServletRequest request);
    void createStore(StoreRequestDto requestDto, MultipartFile files, HttpServletRequest request);
    void updateStore(Long id, StoreRequestDto updateDto, MultipartFile files,
                     FileRequestDto requestDto, HttpServletRequest request) throws Exception;
    void removeStore(HttpServletRequest request);
}
