package com.dessert.gallery.dto.user.request;

import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDto {

    @ApiModelProperty(value = "Email")
    private String email;

    @ApiModelProperty(value = "일반 로그인 패스워드")
    private String password;

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "NORMAL / KAKAO")
    private LoginType loginType;

    public User toEntity() {
        User user = User.builder()
                .uid(String.valueOf(UUID.randomUUID()))
                .password(password)
                .email(email)
                .nickname(nickname)
                .userRole(UserRole.USER)
                .loginType(loginType)
                .deleted(false)
                .build();

        return user;
    }
}
