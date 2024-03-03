package com.dessert.gallery.dto.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FollowResponseDto {

    @Schema(description = "가게 id")
    private Long storeId;

    @Schema(description = "가게 이름")
    private String storeName;

    @Schema(description = "가게 프로필 사진 이름")
    private String fileName;

    @Schema(description = "가게 프로필 사진 URL")
    private String fileUrl;
}
