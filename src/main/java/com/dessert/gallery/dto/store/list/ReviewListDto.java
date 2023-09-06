package com.dessert.gallery.dto.store.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewListDto {

    @Schema(description = "리뷰 작성자 닉네임")
    private String nickname;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 점수")
    private Double score;

    @Schema(description = "리뷰 작성 일자")
    private LocalDateTime createDate;

    @Schema(description = "사진 이름")
    private String fileName;

    @Schema(description = "사진 URL")
    private String fileUrl;
}
