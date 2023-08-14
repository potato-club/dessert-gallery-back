package com.dessert.gallery.dto.calendar;

import com.dessert.gallery.dto.schedule.ScheduleResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class CalendarResponseDto {
    @Schema(description = "캘린더 해당 연도")
    private int year;
    @Schema(description = "캘린더 해당 월")
    private int month;
    @Schema(description = "연월에 해당하는 스케줄 리스트")
    private List<ScheduleResponseDto> scheduleList;

    public CalendarResponseDto(int year, int month, List<ScheduleResponseDto> scheduleList) {
        this.year = year;
        this.month = month;
        this.scheduleList = scheduleList;
    }
}
