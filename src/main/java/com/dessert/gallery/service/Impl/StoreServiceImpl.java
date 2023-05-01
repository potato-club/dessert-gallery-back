package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.StoreService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public StoreResponseDto getStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("store is not exist"));
        StoreResponseDto dto = new StoreResponseDto(store);
        return dto;
    }

    @Override
    public void createStore(StoreRequestDto requestDto, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = new Store(requestDto, user);
        storeRepository.save(store);
    }

    @Override
    public void updateStore(Long id, StoreRequestDto updateDto, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Store store = storeRepository.findById(id).orElseThrow();

        if(store.getUser() != user) {
            throw new RuntimeException("401 권한없음");
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
}
