package com.dessert.gallery.dto.user.response;

import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserProfileResponseDto {

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "로그인 타입", example = "NORMAL / KAKAO")
    private LoginType loginType;

    @Schema(description = "유저 역할", example = "USER / MANAGER")
    private UserRole userRole;

    @Schema(description = "가게 Id", example = "1")
    private Long storeId;

    @Schema(description = "사진 이름")
    private String fileName;

    @Schema(description = "사진 URL")
    private String fileUrl;
}
