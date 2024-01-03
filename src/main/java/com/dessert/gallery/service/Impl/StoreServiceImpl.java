package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.store.StoreOwnerResponseDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.dto.store.map.StoreCoordinate;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.*;
import com.dessert.gallery.service.Interface.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreBoardRepository boardRepository;
    private final ReviewBoardRepository reviewRepository;
    private final SubscribeRepository subscribeRepository;
    private final UserService userService;
    private final CalendarService calendarService;
    private final KakaoMapService mapService;
    private final ImageService imageService;

    @Override
    public StoreOwnerResponseDto getStoreDtoByUser(HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        if (user == null) throw new NotFoundException("존재하지 않는 유저", NOT_FOUND_EXCEPTION);

        Store store = storeRepository.findByUser(user);
        if (store == null) throw new NotFoundException("존재하지 않는 가게입니다", NOT_FOUND_EXCEPTION);

        int postCount = Math.toIntExact(boardRepository.countAllByStore(store));
        int reviewCount = Math.toIntExact(reviewRepository.countByStore(store));

        return new StoreOwnerResponseDto(store, postCount, reviewCount);
    }

    @Override
    public StoreResponseDto getStoreDto(Long id, HttpServletRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 가게입니다", NOT_FOUND_EXCEPTION));
        User user = userService.findUserByToken(request);

        int postCount = Math.toIntExact(boardRepository.countAllByStore(store));
        int followerCount = Math.toIntExact(subscribeRepository.countAllByStoreAndDeletedIsFalse(store));

        StoreResponseDto responseDto = new StoreResponseDto(store, postCount, followerCount);
        if (user != null) {
            boolean isOwner = store.checkOwner(user);
            boolean followState = subscribeRepository.existsByStoreAndUserAndDeletedIsFalse(store, user);
            responseDto.addSubInfo(isOwner, followState);
        }
        return responseDto;
    }

    @Override
    @Transactional
    public void createStore(StoreRequestDto requestDto, MultipartFile file,
                            HttpServletRequest request) {
        try {
            User user = userService.findUserByToken(request);
            if (user.getUserRole() != UserRole.MANAGER)
                throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
            StoreCoordinate coordinate = mapService.getKakaoCoordinate(requestDto.getAddress());
            Store store = new Store(requestDto, coordinate, user);
            if (file != null) {
                List<MultipartFile> files = new ArrayList<>();
                files.add(file);
                List<File> image = imageService.uploadImages(files, store);
                store.setImage(image.get(0));
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
                            MultipartFile file, FileRequestDto requestDto,
                            HttpServletRequest request) throws Exception {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if (store.getUser() != user)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        if (updateDto.getAddress() != null) {
            store.updateCoordinate(mapService.getKakaoCoordinate(updateDto.getAddress()));
        }
        store.updateStore(updateDto);

        List<MultipartFile> files = new ArrayList<>();
        List<FileRequestDto> requestDtoList = new ArrayList<>();
        List<File> resultImages;

        if (file != null) { // 바꿀 사진 존재
            files.add(file);

            if (requestDto == null) { // 원본 없음 => 단순 사진 업로드
                resultImages = imageService.uploadImages(files, store);
                store.setImage(resultImages.get(0));
            }
            if (requestDto != null) { // 원본 있음 => 사진 업데이트
                requestDtoList.add(requestDto);
                resultImages = imageService.updateImages(store, files, requestDtoList);
                store.setImage(resultImages.get(0));
            }
        }
        if (file == null) { // 바꿀 사진 없음
            if (requestDto != null) { // 원본 있음 -> 삭제 요청
                requestDtoList.add(requestDto);

                List<File> newFile = imageService.updateImages(store, files, requestDtoList);

                if (newFile.size() != 0) store.setImage(newFile.get(0));
                else store.setImage(null);
            }
        }
    }

    // 가게 삭제 로직은 좀 더 생각해보기 => 실수로 삭제했을 때 다 날아가면 망함
    // 스케줄러로 삭제 유예 기간을 주던가 해야될듯
    // 현재 FK로 인해 작동 안함
    @Override
    @Transactional
    public void removeStore(HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        if (user == null) throw new NotFoundException("존재하지 않는 유저", NOT_FOUND_EXCEPTION);

        Store store = storeRepository.findByUser(user);
        if (!store.checkOwner(user)) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }

        storeRepository.delete(store);
    }
}
