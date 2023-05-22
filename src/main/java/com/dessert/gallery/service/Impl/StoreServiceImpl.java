package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.UserRole;
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
                .orElseThrow(() -> new RuntimeException("store is not exist"));
        int postCount = Math.toIntExact(boardRepository.countAllByStore(store));
        StoreResponseDto dto = new StoreResponseDto(store, postCount);
        return dto;
    }

    @Override
    public void createStore(StoreRequestDto requestDto, List<MultipartFile> files,
                            HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        if(user.getUserRole() != UserRole.MANAGER) throw new RuntimeException("401 권한없음");
        Store store = new Store(requestDto, user);
        if(files != null) {
            File file = saveImage(files, store);
            store.setImage(file);
        }
        storeRepository.save(store);
    }

    @Override
    public void updateStore(Long id, StoreRequestDto updateDto,
                            List<MultipartFile> files, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if(store.getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        if(files != null) {
            File newFile = updateImage(store, files);
            store.setImage(newFile);
        }
        store.updateStore(updateDto);
    }

    @Override
    public void removeStore(Long id, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if(store.getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        storeRepository.delete(store);
    }

    private File saveImage(List<MultipartFile> images, Store store) {
        try {
            List<File> files = s3Service.uploadImages(images, store);
            return files.get(0);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 에러");
        }
    }

    private File updateImage(Store store, List<MultipartFile> images) {
        try {
            List<File> files = s3Service.updateFiles(store, images);
            return files.get(0);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업데이트 에러");
        }
    }
}
