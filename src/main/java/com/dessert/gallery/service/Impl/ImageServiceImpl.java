package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.S3Exception;
import com.dessert.gallery.service.Interface.ImageService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.dessert.gallery.error.ErrorCode.RUNTIME_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final S3Service s3Service;

    @Override
    public File saveImage(List<MultipartFile> images, Store store) {
        try {
            List<File> files = s3Service.uploadImages(images, store);
            return files.get(0);
        } catch (IOException e) {
            log.error("이미지 업로드 에러 발생");
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }

    @Override
    public List<File> saveImage(List<MultipartFile> images, StoreBoard board) {
        try {
            return s3Service.uploadImages(images, board);
        } catch (IOException e) {
            log.error("이미지 업로드 에러 발생");
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }

    @Override
    public List<File> saveImage(List<MultipartFile> images, ReviewBoard review) {
        try {
            return s3Service.uploadImages(images, review);
        } catch (IOException e) {
            log.error("이미지 업로드 에러 발생");
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }

    @Override
    public File updateImage(Store store, List<MultipartFile> images, List<FileRequestDto> requestDto) {
        try {
            List<File> files = s3Service.updateFiles(store, images, requestDto);
            return files.get(0);
        } catch (IOException e) {
            log.error("이미지 업데이트 에러 발생");
            throw new S3Exception("이미지 업데이트 에러", RUNTIME_EXCEPTION);
        }
    }

    @Override
    public List<File> updateImage(StoreBoard board, List<MultipartFile> images, List<FileRequestDto> requestDto) {
        try {
            return s3Service.updateFiles(board, images, requestDto);
        } catch (IOException e) {
            log.error("이미지 업데이트 에러 발생");
            throw new S3Exception("이미지 업데이트 에러", RUNTIME_EXCEPTION);
        }
    }
}
