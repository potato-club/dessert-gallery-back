package com.dessert.gallery.dto.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponseDto {

    @Schema(description = "가게 이름")
    private String storeName;

    @Schema(description = "유저 닉네임")
    private String nickname;

    @Schema(description = "프로필 사진 이름")
    private String fileName;

    @Schema(description = "프로필 사진 URL")
    private String fileUrl;
}
