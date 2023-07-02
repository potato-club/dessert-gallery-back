package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.S3Exception;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.StoreService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreBoardRepository boardRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @Override
    public StoreResponseDto getStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 가게입니다", NOT_FOUND_EXCEPTION));
        int postCount = Math.toIntExact(boardRepository.countAllByStore(store));
        StoreResponseDto dto = new StoreResponseDto(store, postCount);
        return dto;
    }

    @Override
    public void createStore(StoreRequestDto requestDto, List<MultipartFile> files,
                            HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        if(user.getUserRole() != UserRole.MANAGER)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        Store store = new Store(requestDto, user);
        if(files != null) {
            File file = saveImage(files, store);
            store.setImage(file);
        }
        storeRepository.save(store);
    }

    @Override
    public void updateStore(Long id, StoreRequestDto updateDto,
                            List<MultipartFile> files, List<FileRequestDto> requestDto,
                            HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if(store.getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        if(files != null) {
            File newFile = updateImage(store, files, requestDto);
            store.setImage(newFile);
        }
    }

    @Override
    public void removeStore(Long id, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if(store.getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        storeRepository.delete(store);
    }

    private File saveImage(List<MultipartFile> images, Store store) {
        try {
            List<File> files = s3Service.uploadImages(images, store);
            return files.get(0);
        } catch (IOException e) {
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }

    private File updateImage(Store store, List<MultipartFile> images, List<FileRequestDto> requestDto) {
        try {
            List<File> files = s3Service.updateFiles(store, images, requestDto);
            return files.get(0);
        } catch (IOException e) {
            throw new S3Exception("이미지 업데이트 에러", RUNTIME_EXCEPTION);
        }
    }
}
