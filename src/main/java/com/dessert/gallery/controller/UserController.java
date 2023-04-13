package com.dessert.gallery.controller;

import com.dessert.gallery.dto.UserResponseDto;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/login/kakao")
    public UserResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        return userService.kakaoLogin(code, response);
    }
}
