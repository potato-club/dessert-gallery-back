package com.dessert.gallery.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKakaoResponseDto {

    @Schema(description = "Email")
    private String email;

    @Schema(description = "응답 코드", example = "200_OK / 201_CREATED")
    private String responseCode;
}
