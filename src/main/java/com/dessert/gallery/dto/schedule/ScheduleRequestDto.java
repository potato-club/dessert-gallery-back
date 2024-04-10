package com.dessert.gallery.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ScheduleRequestDto {
    @Schema(description = "스케줄 날짜", example = "2024-01-01")
    @NotBlank(message = "날짜 정보는 필수입니다.")
    private String date;
    @Schema(description = "HOLIDAY = 2 / EVENT = 3")
    @NotNull(message = "key 값을 입력해주세요.")
    private Integer key;
}
