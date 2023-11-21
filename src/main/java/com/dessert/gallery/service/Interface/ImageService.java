package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    File saveImage(List<MultipartFile> images, Store store);
    List<File> saveImage(List<MultipartFile> images, StoreBoard board);
    List<File> saveImage(List<MultipartFile> images, ReviewBoard review);
    File updateImage(Store store, List<MultipartFile> images, List<FileRequestDto> requestDto);
    List<File> updateImage(StoreBoard board, List<MultipartFile> images, List<FileRequestDto> requestDto);
}
