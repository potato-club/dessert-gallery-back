package com.dessert.gallery.dto.memo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoRequestDto {
    @Schema(description = "메모 내용")
    private String content;
    @Schema(description = "해당 연도 (yyyy)", example = "2023")
    private String year;
    @Schema(description = "해당 월 (MM)", example = "01")
    private String month;
}
