package com.dessert.gallery.dto.schedule;

import com.dessert.gallery.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ScheduleResponseDto {
    @Schema(description = "스케줄 날짜")
    private String date;

    @Schema(description = "휴무일 여부")
    private boolean holiday;

    @Schema(description = "픽업 예약 여부")
    private boolean reservation;

    @Schema(description = "이벤트 여부")
    private boolean event;


    public ScheduleResponseDto(Schedule schedule) {
        this.date = parsingDateTime(schedule.getDateTime());
    }

    public void setHoliday() {
        this.holiday = true;
    }

    public void setReservation() {
        this.reservation = true;
    }

    public void setEvent() {
        this.event = true;
    }

    private String parsingDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
