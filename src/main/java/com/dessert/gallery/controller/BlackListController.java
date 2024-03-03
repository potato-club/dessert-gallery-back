package com.dessert.gallery.controller;

import com.dessert.gallery.dto.blacklist.BlackListRequestDto;
import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
import com.dessert.gallery.service.Interface.BlackListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mypage")
@Tag(name = "BlackList Controller", description = "블랙리스트 API")
public class BlackListController {

    private final BlackListService blackListService;

    @Operation(summary = "블랙리스트 등록 API")
    @PostMapping("/blacklist")
    public ResponseEntity<String> addBlackList(@RequestBody BlackListRequestDto dto, HttpServletRequest request) {
        blackListService.addBlackList(dto, request);
        return ResponseEntity.ok("해당 유저를 블랙했습니다.");
    }

    @Operation(summary = "블랙리스트 취소 API")
    @PutMapping("/blacklist")
    public ResponseEntity<String> removeBlackList(@RequestBody BlackListRequestDto dto, HttpServletRequest request) {
        blackListService.removeBlackList(dto, request);
        return ResponseEntity.ok("해당 유저를 블랙 해제했습니다.");
    }

    @Operation(summary = "내가 등록한 블랙리스트 출력 API")
    @GetMapping("/blacklist")
    public Page<BlackListResponseDto> getBlackList(@RequestParam int page, HttpServletRequest request) {
        return blackListService.getBlackList(page, request);
    }
}
