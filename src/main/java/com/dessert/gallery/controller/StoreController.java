package com.dessert.gallery.controller;

import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.service.Interface.StoreService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Api(tags = {"Store Controller"})
public class StoreController {
    private final StoreService storeService;

    @Operation(summary = "가게 정보 조회 API")
    @GetMapping("/{storeId}")
    public StoreResponseDto getStore(@PathVariable(name = "storeId") Long storeId) {
        return storeService.getStore(storeId);
    }

    @Operation(summary = "가게 생성 API")
    @PostMapping("")
    public ResponseEntity<String> createStore(@RequestPart StoreRequestDto requestDto,
                                              @RequestPart(required = false) List<MultipartFile> images,
                                              HttpServletRequest request) {
        storeService.createStore(requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("가게 생성 완료");
    }

    @Operation(summary = "가게 정보 수정 API")
    @PutMapping("/{storeId}")
    public ResponseEntity<String> updateStore(@PathVariable(name = "storeId") Long id,
                                              @RequestPart StoreRequestDto updateDto,
                                              @RequestPart List<MultipartFile> images,
                                              HttpServletRequest request) {
        storeService.updateStore(id, updateDto, images, request);
        return ResponseEntity.ok("가게 정보 수정 완료");
    }

    @Operation(summary = "가게 삭제 API")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable(name = "storeId") Long id, HttpServletRequest request) {
        storeService.removeStore(id, request);
        return ResponseEntity.ok("가게 삭제 완료");
    }
}
