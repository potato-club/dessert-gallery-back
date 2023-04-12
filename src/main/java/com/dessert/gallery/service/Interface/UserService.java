package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.UserResponseDto;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    UserResponseDto kakaoLogin(String code, HttpServletResponse response);
}
