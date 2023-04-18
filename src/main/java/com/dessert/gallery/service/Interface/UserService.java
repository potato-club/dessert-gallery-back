package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.user.OwnerSignUpRequestDto;
import com.dessert.gallery.dto.user.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.UserKakaoResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    UserKakaoResponseDto kakaoLogin(String code, HttpServletResponse response);

    void userSignUp(UserSignUpRequestDto requestDto, HttpServletResponse response);

    void ownerSignUp(OwnerSignUpRequestDto requestDto, HttpServletResponse response);

    void updateUser(HttpServletRequest request);
}
