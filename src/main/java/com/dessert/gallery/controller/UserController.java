package com.dessert.gallery.controller;

import com.dessert.gallery.dto.user.request.UserLoginRequestDto;
import com.dessert.gallery.dto.user.request.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.dto.user.response.UserKakaoResponseDto;
import com.dessert.gallery.dto.user.response.UserLoginResponseDto;
import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.service.Interface.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Api(tags = {"Users & Authorization Controller"})
public class UserController {

    private final UserService userService;

    @Operation(summary = "카카오 로그인 API")
    @GetMapping("/login/kakao")
    public UserKakaoResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        return userService.kakaoLogin(code, response);
    }

    @Operation(summary = "일반 로그인 API")
    @GetMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @Operation(summary = "회원가입 API")
    @PostMapping("/signUp")
    public ResponseEntity<String> userSignUp(@RequestBody UserSignUpRequestDto requestDto, HttpServletResponse response) {
        userService.signUp(requestDto, response);
        return ResponseEntity.ok("회원가입 완료.");
    }

    @Operation(summary = "내 정보 수정 API")
    @PutMapping("")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequestDto requestDto, HttpServletRequest request) {
        userService.updateUser(requestDto, request);
        return ResponseEntity.ok("내 정보 업데이트 완료.");
    }

    @Operation(summary = "내 정보 확인 API")
    @GetMapping("")
    public UserProfileResponseDto viewProfile(HttpServletRequest request) {
        return userService.viewProfile(request);
    }

    @Operation(summary = "로그아웃 API")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<String> withdrawalMembership(HttpServletRequest request) {
        userService.withdrawalMembership(request);
        return ResponseEntity.ok("회원탈퇴 처리 되었습니다");
    }
}
