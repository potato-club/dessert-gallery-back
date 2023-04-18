package com.dessert.gallery.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKakaoResponseDto {
    private String email;
    private String responseCode;
}
