package com.dessert.gallery.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    @Schema(description = "리뷰 내용")
    private String content;
    @Schema(description = "리뷰 점수")
    private Double score;
}
