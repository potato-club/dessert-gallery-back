package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.user.request.OwnerSignUpRequestDto;
import com.dessert.gallery.dto.user.request.UserLoginRequestDto;
import com.dessert.gallery.dto.user.request.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.response.UserKakaoResponseDto;
import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.dto.user.response.UserLoginResponseDto;
import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.Jwt.RedisService;
import com.dessert.gallery.service.KakaoApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import static com.dessert.gallery.error.ErrorCode.ACCESS_DENIED_EXCEPTION;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KakaoApi kakaoApi;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserKakaoResponseDto kakaoLogin(String code, HttpServletResponse response) {
        String access_token = kakaoApi.getAccessToken(code);
        String email = kakaoApi.getUserInfo(access_token);

        if (userRepository.existsByEmailAndDeleted(email, false)) {
            this.setJwtTokenInHeader(email, response);

            return UserKakaoResponseDto.builder()
                    .responseCode("200_OK")
                    .build();
        }

        return UserKakaoResponseDto.builder()
                .email(email)
                .responseCode("201_CREATED")
                .build();
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response) {
        if (!userRepository.existsByEmail(requestDto.getEmail())
                && userRepository.existsByEmailAndDeleted(requestDto.getEmail(), true)) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }

        this.setJwtTokenInHeader(requestDto.getEmail(), response);

        return UserLoginResponseDto.builder()
                .responseCode("200_OK")
                .build();
    }

    @Override
    public void userSignUp(UserSignUpRequestDto requestDto, HttpServletResponse response) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }

        if (requestDto.getLoginType().equals(LoginType.KAKAO)) {
            User user = requestDto.toEntity();
            userRepository.save(user);
        } else if (requestDto.getLoginType().equals(LoginType.NORMAL)) {
            requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            User user = requestDto.toEntity();
            userRepository.save(user);
        } else {
            throw new UnAuthorizedException("401_NOT_ALLOW", ErrorCode.NOT_ALLOW_WRITE_EXCEPTION);
        }

        this.setJwtTokenInHeader(requestDto.getEmail(), response);
    }

    @Override
    public void ownerSignUp(OwnerSignUpRequestDto requestDto, HttpServletResponse response) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }

        if (requestDto.getLoginType().equals(LoginType.KAKAO)) {
            User user = requestDto.toEntity();
            userRepository.save(user);
        } else if (requestDto.getLoginType().equals(LoginType.NORMAL)) {
            requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            User user = requestDto.toEntity();
            userRepository.save(user);
        } else {
            throw new UnAuthorizedException("401_NOT_ALLOW", ErrorCode.NOT_ALLOW_WRITE_EXCEPTION);
        }

        this.setJwtTokenInHeader(requestDto.getEmail(), response);
    }

    @Override
    public void updateUser(UserUpdateRequestDto requestDto, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));

        User user = userRepository.findByEmail(email).orElseThrow();
        user.update(requestDto);
    }

    @Override
    public UserProfileResponseDto viewProfile(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        UserProfileResponseDto responseDto = UserProfileResponseDto.builder()
                .nickname(user.getNickname())
                .loginType(user.getLoginType())
                .userRole(user.getUserRole())
                .storeAddress(user.getStoreAddress())
                .storePhoneNumber(user.getStorePhoneNumber())
                .build();

        return responseDto;
    }

    @Override
    public void logout(HttpServletRequest request) {
        redisService.delValues(jwtTokenProvider.resolveRefreshToken(request));
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(request));
    }

    @Override
    public void withdrawalMembership(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        user.setDeleted(true);
        this.logout(request);
    }

    @Override
    public void cancelMembership() {

    }

    public void setJwtTokenInHeader(String email, HttpServletResponse response) {
        UserRole userRole = userRepository.findByEmail(email).get().getUserRole();

        String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        redisService.setValues(refreshToken, email);
    }
}
