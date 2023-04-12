package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.UserResponseDto;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.Jwt.RedisService;
import com.dessert.gallery.service.KakaoApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KakaoApi kakaoApi;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public UserResponseDto kakaoLogin(String code, HttpServletResponse response) {
        String access_token = kakaoApi.getAccessToken(code);
        String email = kakaoApi.getUserInfo(access_token);

        if (userRepository.existsByEmail(email)) {
            UserRole userRole = userRepository.findByEmail(email).get().getUserRole();

            String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
            String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

            jwtTokenProvider.setHeaderAccessToken(response, accessToken);
            jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

            redisService.setValues(refreshToken, email);

            return UserResponseDto.builder()
                    .responseCode("200_OK")
                    .build();
        }

        return UserResponseDto.builder()
                .email(email)
                .responseCode("201_CREATED")
                .build();
    }
}
