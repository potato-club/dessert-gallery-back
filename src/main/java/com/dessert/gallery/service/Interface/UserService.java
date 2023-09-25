package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.user.request.UserLoginRequestDto;
import com.dessert.gallery.dto.user.request.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.response.UserKakaoResponseDto;
import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.dto.user.response.UserLoginResponseDto;
import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UserService {
    UserKakaoResponseDto kakaoLogin(String code, HttpServletResponse response);

    UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response);

    void signUp(UserSignUpRequestDto requestDto, HttpServletResponse response);

    boolean isNicknameDuplicated(String nickname);

    void updateUser(UserUpdateRequestDto requestDto, HttpServletRequest request) throws IOException;

    UserProfileResponseDto viewProfile(HttpServletRequest request);

    void logout(HttpServletRequest request);

    void withdrawalMembership(HttpServletRequest request);

    User findUserByToken(HttpServletRequest request);

    void reissueToken(HttpServletRequest request, HttpServletResponse response);
}
