package com.dessert.gallery.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserCancel {

    @Schema(description = "회원 탈퇴한 Email")
    private final String email;

    @Schema(description = "철회 동의 여부")
    private final boolean agreement;

    @Builder
    public UserCancel(String email, boolean agreement) {
        this.email = email;
        this.agreement = agreement;
    }
}
