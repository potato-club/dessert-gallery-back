package com.dessert.gallery.dto.user.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponseDto {

    @ApiModelProperty(value = "응답 코드", example = "200_OK")
    private String responseCode;
}
