package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.dto.store.map.StoreCoordinate;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.S3Exception;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.*;
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
    private final CalendarService calendarService;
    private final S3Service s3Service;
    private final KakaoMapService mapService;

    @Override
    public Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 가게입니다", NOT_FOUND_EXCEPTION));
    }

    @Override
    public StoreResponseDto getStoreDto(Long id) {
        Store store = getStore(id);
        int postCount = Math.toIntExact(boardRepository.countAllByStore(store));
        return new StoreResponseDto(store, postCount);
    }

    @Override
    public void createStore(StoreRequestDto requestDto, List<MultipartFile> files,
                            HttpServletRequest request) {
        try {
            User user = userService.findUserByToken(request);
            if(user.getUserRole() != UserRole.MANAGER)
                throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
            StoreCoordinate coordinate = mapService.getKakaoCoordinate(requestDto.getAddress());
            Store store = new Store(requestDto, coordinate, user);
            if(files != null) {
                File file = saveImage(files, store);
                store.setImage(file);
            }
            Store saveStore = storeRepository.save(store);
            calendarService.createCalendar(saveStore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
