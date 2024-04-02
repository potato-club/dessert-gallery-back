package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.user.request.UserLoginRequestDto;
import com.dessert.gallery.dto.user.request.UserSignUpRequestDto;
import com.dessert.gallery.dto.user.response.UserKakaoResponseDto;
import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.dto.user.response.UserLoginResponseDto;
import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.File.FileRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.repository.User.UserRepository;
import com.dessert.gallery.service.Interface.ImageService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.Jwt.RedisJwtService;
import com.dessert.gallery.service.KakaoApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dessert.gallery.error.ErrorCode.ACCESS_DENIED_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final StoreRepository storeRepository;
    private final ImageService imageService;
    private final KakaoApi kakaoApi;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisJwtService redisJwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserKakaoResponseDto kakaoLogin(String code, HttpServletResponse response) {
        String access_token = kakaoApi.getAccessToken(code);
        String email = kakaoApi.getUserInfo(access_token);

        if (userRepository.existsByEmailAndDeleted(email, false)) {
            this.setJwtTokenInHeader(email, response);

            return UserKakaoResponseDto.builder()
                    .responseCode("200")
                    .build();
        }

        if (userRepository.existsByEmailAndDeletedIsTrue(email)) {
            User user = userRepository.findByEmail(email).orElseThrow();
            user.setDeleted(false);
            this.setJwtTokenInHeader(email, response);

            return UserKakaoResponseDto.builder()
                    .responseCode("2000")   // 회원가입 필요
                    .build();
        }

        return UserKakaoResponseDto.builder()
                .email(email)
                .responseCode("201")
                .build();
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto, HttpServletResponse response) {
        if (!userRepository.existsByEmailAndDeletedAndEmailOtp(requestDto.getEmail(), false, true)) {
            if (!userRepository.existsByEmail(requestDto.getEmail())) {
                return UserLoginResponseDto.builder()
                        .responseCode("2001")   // 회원이 아닐 경우
                        .build();
            } else if (userRepository.existsByEmailAndDeletedIsTrue(requestDto.getEmail())) {
                return UserLoginResponseDto.builder()
                        .responseCode("2002")   // 탈퇴한 회원인 경우
                        .build();
            } else if (userRepository.existsByEmailAndEmailOtpIsFalse(requestDto.getEmail())) {
                userRepository.delete(userRepository.findByEmail(requestDto.getEmail()).orElseThrow());
                return UserLoginResponseDto.builder()
                        .responseCode("2003")   // 2차 인증이 제대로 이루어지지 않은 경우 -> 처음부터 회원 가입 필요
                        .build();
            }
        }

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow();

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);    // 패스워드 불일치
        }

        this.setJwtTokenInHeader(requestDto.getEmail(), response);

        return UserLoginResponseDto.builder()
                .responseCode("200")
                .build();
    }

    @Override
    @Transactional
    public void signUp(UserSignUpRequestDto requestDto, HttpServletResponse response) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UnAuthorizedException("401", ACCESS_DENIED_EXCEPTION);
        }

        if (requestDto.getLoginType().equals(LoginType.KAKAO)) {    // 카카오 회원가입은 바로 저장 후 토큰 발급
            User user = requestDto.toEntity();
            user.setEmailOtp(true);

            userRepository.save(user);
            this.setJwtTokenInHeader(requestDto.getEmail(), response);
        } else if (requestDto.getLoginType().equals(LoginType.NORMAL)) {    // 일반 회원가입은 2차 인증 후 토큰 발급 예정
            requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

            User user = requestDto.toEntity();
            user.setEmailOtp(false);

            userRepository.save(user);
        } else {
            throw new UnAuthorizedException("401_NOT_ALLOW", ErrorCode.NOT_ALLOW_WRITE_EXCEPTION);
        }
    }

    @Override
    public boolean isNicknameDuplicated(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateRequestDto requestDto, HttpServletRequest request) throws IOException {
        User user = findUserByToken(request);

        if (requestDto.getFile().size() != 0) {
            List<FileRequestDto> fileList = new ArrayList<>();
            FileRequestDto fileDto = FileRequestDto.builder()
                    .fileName(requestDto.getFileName())
                    .fileUrl(requestDto.getFileUrl())
                    .deleted(true)
                    .build();

            fileList.add(fileDto);
            imageService.updateImages(user, requestDto.getFile(), fileList);
        }

        user.update(requestDto);
    }

    @Override
    public UserProfileResponseDto viewProfile(HttpServletRequest request) {
        User user = this.findUserByToken(request);

        if (user.getUserRole().equals(UserRole.MANAGER)) {
            Store store = storeRepository.findByUser(user);
            return fileRepository.getUserProfileAsManager(user, store);
        }

        return fileRepository.getUserProfileAsUser(user);
    }

    @Override
    public void logout(HttpServletRequest request) {
        redisJwtService.delValues(jwtTokenProvider.resolveRefreshToken(request));
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(request));
    }

    @Override
    @Transactional
    public void withdrawalMembership(HttpServletRequest request) {
        User user = findUserByToken(request);

        user.setDeleted(true);
        this.logout(request);
    }

    @Override
    public User findUserByToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveAccessToken(request);
        return token == null ? null : userRepository.findByEmail(jwtTokenProvider.getUserEmail(token)).orElseThrow();
    }

    @Override
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);
        String newRefreshToken = jwtTokenProvider.reissueRefreshToken(refreshToken);

        jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, newRefreshToken);
    }

    public void setJwtTokenInHeader(String email, HttpServletResponse response) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new NotFoundException("Not Found User", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        UserRole userRole = user.get().getUserRole();

        String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        redisJwtService.setValues(refreshToken, email);
    }
}
