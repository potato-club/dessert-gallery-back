package com.dessert.gallery.dto.schedule;

import com.dessert.gallery.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ReservationResponseDto implements Comparable<ReservationResponseDto> {
    @Schema(description = "픽업 일정 id")
    private Long id;

    @Schema(description = "픽업 시간", example = "17:30")
    private String dateTime;

    @Schema(description = "손님 닉네임")
    private String client;

    public ReservationResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.dateTime = schedule.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.client = schedule.getClient();
    }

    @Override
    public int compareTo(ReservationResponseDto o) {
        return this.dateTime.compareTo(o.getDateTime());
    }
}