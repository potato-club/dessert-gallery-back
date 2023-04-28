package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.user.request.UserLoginRequestDto;
import com.dessert.gallery.dto.user.request.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.response.UserKakaoResponseDto;
import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.dto.user.response.UserLoginResponseDto;
import com.dessert.gallery.dto.user.response.UserProfileResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    UserKakaoResponseDto kakaoLogin(String code, HttpServletResponse response);

    UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response);

    void signUp(UserSignUpRequestDto requestDto, HttpServletResponse response);

    void updateUser(UserUpdateRequestDto requestDto, HttpServletRequest request);

    UserProfileResponseDto viewProfile(HttpServletRequest request);

    void logout(HttpServletRequest request);

    void withdrawalMembership(HttpServletRequest request);

    void cancelMembership();
}
