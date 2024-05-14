package com.dessert.gallery.controller;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.user.request.UserCancel;
import com.dessert.gallery.dto.user.request.UserLoginRequestDto;
import com.dessert.gallery.dto.user.request.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.dto.user.response.UserKakaoResponseDto;
import com.dessert.gallery.dto.user.response.UserLoginResponseDto;
import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.service.Interface.BookmarkService;
import com.dessert.gallery.service.Interface.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Tag(name = "Users & Authorization Controller", description = "유저 및 인증 API")
public class UserController {

    private final UserService userService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "카카오 로그인 API")
    @GetMapping("/login/kakao")
    public UserKakaoResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        return userService.kakaoLogin(code, response);
    }

    @Operation(summary = "일반 로그인 API")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @Operation(summary = "회원가입 API")
    @PostMapping("/signup")
    public ResponseEntity<String> userSignUp(@RequestBody UserSignUpRequestDto requestDto, HttpServletResponse response) {
        userService.signUp(requestDto, response);
        return ResponseEntity.ok("회원가입 완료.");
    }

    @Operation(summary = "닉네임 중복 확인 API")
    @GetMapping("/duplication/nickname")
    public ResponseEntity<?> isNicknameDuplicated(@RequestParam String nickname) {
        boolean isDuplicated = userService.isNicknameDuplicated(nickname);
        return ResponseEntity.ok().body(isDuplicated);
    }

    @Operation(summary = "내 정보 수정 API")
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateUser(UserUpdateRequestDto requestDto, HttpServletRequest request) throws IOException {
        userService.updateUser(requestDto, request);
        return ResponseEntity.ok("내 정보 업데이트 완료.");
    }

    @Operation(summary = "내 정보 확인 API")
    @GetMapping("")
    public UserProfileResponseDto viewProfile(HttpServletRequest request) {
        return userService.viewProfile(request);
    }

    @Operation(summary = "북마크 게시글 확인 API")
    @GetMapping("/bookmark")
    public Slice<BoardListResponseDto> viewBookmark(HttpServletRequest request,
                                                    @Parameter(name = "page", description = "페이지 정보")
                                                   @RequestParam(value = "page",
                                                           required = false,
                                                           defaultValue = "1") int page) {
        return bookmarkService.getBookmarks(request, page);
    }

    @Operation(summary = "로그아웃 API")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @Operation(summary = "회원탈퇴 API")
    @PutMapping("/withdrawal")
    public ResponseEntity<String> withdrawalMembership(HttpServletRequest request) {
        userService.withdrawalMembership(request);
        return ResponseEntity.ok("회원탈퇴 처리 되었습니다");
    }

    @Operation(summary = "회원탈퇴 취소 API")
    @PutMapping("/cancel")
    public ResponseEntity<String> cancelWithdrawal(@RequestBody UserCancel cancelDto) {
        userService.cancelWithdrawal(cancelDto.getEmail(), cancelDto.isAgreement());
        return ResponseEntity.ok("회원탈퇴 처리가 취소되었습니다");
    }

    @Operation(summary = "토큰 재발급 API")
    @GetMapping("/reissue")
    public ResponseEntity<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        userService.reissueToken(request, response);
        return ResponseEntity.ok("토큰 재발급이 완료되었습니다");
    }

    @Operation(summary = "토큰 상태 확인 API")
    @GetMapping("/check")
    public ResponseEntity<String> validateToken() {
        return ResponseEntity.ok("Accessed Token");
    }
}
