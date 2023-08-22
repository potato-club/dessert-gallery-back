package com.dessert.gallery.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleRequestDto {
    @Schema(description = "스케줄 날짜", example = "2024-01-01")
    private String date;
    @Schema(description = "RESERVATION = 1 / HOLIDAY = 2 / EVENT = 3")
    private Integer key;
}
