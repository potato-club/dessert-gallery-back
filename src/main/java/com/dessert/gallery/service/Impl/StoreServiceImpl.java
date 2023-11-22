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
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.repository.SubscribeRepository;
import com.dessert.gallery.service.Interface.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreBoardRepository boardRepository;
    private final SubscribeRepository subscribeRepository;
    private final UserService userService;
    private final CalendarService calendarService;
    private final KakaoMapService mapService;
    private final ImageService imageService;

    @Override
    public Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 가게입니다", NOT_FOUND_EXCEPTION));
    }

    @Override
    public Store getStoreByUser(User user) {
        return storeRepository.findByUser(user);
    }

    @Override
    public StoreResponseDto getStoreDto(Long id, HttpServletRequest request) {
        Store store = getStore(id);
        int postCount = Math.toIntExact(boardRepository.countAllByStore(store));
        int followerCount = Math.toIntExact(subscribeRepository.countAllByStore(store));

        StoreResponseDto responseDto = new StoreResponseDto(store, postCount, followerCount);
        User user = userService.findUserByToken(request);
        if (user != null) {
            boolean isOwner = store.checkOwner(user);
            boolean followState = subscribeRepository.existsByStoreAndUserAndDeletedIsFalse(store, user);
            responseDto.addSubInfo(isOwner, followState);
        }
        return responseDto;
    }

    @Override
    @Transactional
    public void createStore(StoreRequestDto requestDto, List<MultipartFile> files,
                            HttpServletRequest request) {
        try {
            User user = userService.findUserByToken(request);
            if (user.getUserRole() != UserRole.MANAGER)
                throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
            StoreCoordinate coordinate = mapService.getKakaoCoordinate(requestDto.getAddress());
            Store store = new Store(requestDto, coordinate, user);
            if (files != null) {
                List<File> file = imageService.uploadImages(files, store);
                store.setImage(file.get(0));
            }
            Store saveStore = storeRepository.save(store);
            calendarService.createCalendar(saveStore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void updateStore(Long id, StoreRequestDto updateDto,
                            List<MultipartFile> files, List<FileRequestDto> requestDto,
                            HttpServletRequest request) throws Exception {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if (store.getUser() != user)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        if (updateDto.getAddress() != null) {
            store.updateCoordinate(mapService.getKakaoCoordinate(updateDto.getAddress()));
        }
        store.updateStore(updateDto);

        if (files != null) {
            List<File> newFile = imageService.updateImages(store, files, requestDto);
            store.setImage(newFile.get(0));
        }
    }

    @Override
    @Transactional
    public void removeStore(Long id, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();
        if (store.getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        storeRepository.delete(store);
    }
}
