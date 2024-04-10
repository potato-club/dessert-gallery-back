package com.dessert.gallery.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    @Schema(description = "리뷰 내용")
    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 10, max = 500, message = "내용은 10자 이상 500자 이하로 작성해야 됩니다.")
    private String content;

    @Schema(description = "리뷰 점수")
    @NotNull(message = "점수는 필수입니다.")
    @Min(value = 0, message = "최소 0점까지 가능합니다.")
    @Max(value = 5, message = "최대 5점까지 가능합니다.")
    private Double score;
}
