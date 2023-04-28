package com.dessert.gallery.dto.user.response;

import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "로그인 타입", example = "NORMAL / KAKAO")
    private LoginType loginType;

    @ApiModelProperty(value = "유저 역할", example = "USER / MANANGER")
    private UserRole userRole;
}
