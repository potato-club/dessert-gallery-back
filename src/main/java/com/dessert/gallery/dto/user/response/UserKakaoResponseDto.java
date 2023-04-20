package com.dessert.gallery.dto.user.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKakaoResponseDto {

    @ApiModelProperty(value = "Email")
    private String email;

    @ApiModelProperty(value = "응답 코드", example = "200_OK / 201_CREATED")
    private String responseCode;
}
