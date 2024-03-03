package com.dessert.gallery.dto.blacklist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BlackListResponseDto {

    @Schema(description = "등록된 유저 닉네임")
    private String userName;

    @Schema(description = "등록된 유저 프로필 사진 이름")
    private String fileName;

    @Schema(description = "등록된 유저 프로필 사진 URL")
    private String fileUrl;
}
