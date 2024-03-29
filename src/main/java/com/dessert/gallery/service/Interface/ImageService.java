package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService { // 기존 S3Service 클래스를 인터페이스와 구현체로 변경함.

    List<File> uploadImages(List<MultipartFile> files, Object entity) throws IOException;

    List<File> updateImages(Object entity, List<MultipartFile> files, List<FileRequestDto> requestDto) throws IOException;

    byte[] downloadImage(String key) throws IOException;
}
