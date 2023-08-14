package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.entity.Store;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StoreService {
    Store getStore(Long id);
    StoreResponseDto getStoreDto(Long id);
    void createStore(StoreRequestDto requestDto, List<MultipartFile> files, HttpServletRequest request);
    void updateStore(Long id, StoreRequestDto updateDto, List<MultipartFile> files,
                     List<FileRequestDto> requestDto, HttpServletRequest request);
    void removeStore(Long id, HttpServletRequest request);
}
