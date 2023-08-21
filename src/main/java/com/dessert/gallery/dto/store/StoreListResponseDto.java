package com.dessert.gallery.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StoreListResponseDto {

    @Schema(description = "가게 ID")
    private Long id;

    @Schema(description = "가게 이름")
    private String name;

    @Schema(description = "가게 소개")
    private String content;

    @Schema(description = "가게 주소")
    private String address;

    @Schema(description = "가게 프로필 이미지 이름")
    private String fileName;

    @Schema(description = "가게 프로필 이미지 URL")
    private String fileUrl;

    @Schema(description = "가게 평균 점수")
    private Double score;

    @Schema(description = "팔로우를 맺은 가게 표시")
    private Long followId;

    @Schema(description = "가게 평균 점수")
    private LocalDateTime storeBoardModifiedDate;
}
