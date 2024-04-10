package com.dessert.gallery.dto.memo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemoRequestDto {
    @Schema(description = "메모 내용")
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    @Schema(description = "해당 연도 (yyyy)", example = "2023")
    @NotBlank(message = "year 값은 필수입니다.")
    private String year;
    @Schema(description = "해당 월 (MM)", example = "01")
    @NotBlank(message = "month 값은 필수입니다.")
    private String month;
}
