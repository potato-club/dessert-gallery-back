package com.dessert.gallery.dto.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {

    @ApiModelProperty(value = "Email")
    private String email;

    @ApiModelProperty(value = "일반 로그인 패스워드")
    private String password;
}
