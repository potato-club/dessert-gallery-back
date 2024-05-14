package com.dessert.gallery.controller;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import com.dessert.gallery.service.Interface.FollowService;
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
@Tag(name = "Follow Controller", description = "팔로우 API")
public class FollowContoller {

    private final FollowService followService;

    @Operation(summary = "팔로우 API")
    @PostMapping("/follow/{storeId}")
    public ResponseEntity<String> addStoreFollowing(@PathVariable Long storeId, HttpServletRequest request) {
        followService.addStoreFollowing(storeId, request);
        return ResponseEntity.ok("해당 가게를 팔로우했습니다.");
    }

    @Operation(summary = "언팔로우 API")
    @PutMapping("/follow/{storeId}")
    public ResponseEntity<String> removeStoreFollowing(@PathVariable Long storeId, HttpServletRequest request) {
        followService.removeFollowing(storeId, request);
        return ResponseEntity.ok("해당 손님/가게를 언팔로우했습니다.");
    }

    @Operation(summary = "내 팔로우 목록 출력 API")
    @GetMapping("/follow")
    public Page<FollowResponseDto> getFollowingList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                    HttpServletRequest request) {
        return followService.getFollowingList(page, request);
    }
}
