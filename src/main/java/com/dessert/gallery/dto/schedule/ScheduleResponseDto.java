package com.dessert.gallery.dto.schedule;

import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class ScheduleResponseDto {
    @Schema(description = "스케줄 id")
    private Long id;
    @Schema(description = "스케줄 날짜")
    private String date;
    @Schema(description = "스케줄 타입 (RESERVATION / HOLIDAY / EVENT)")
    private ScheduleType type;

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.date = parsingDateTime(schedule.getDateTime());
        this.type = schedule.getType();
    }

    private String parsingDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
