package com.dessert.gallery.dto.schedule;

import com.dessert.gallery.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationResponseForChat implements Comparable<ReservationResponseForChat> {
    @Schema(description = "예약 스케줄 id")
    private Long id;

    @Schema(description = "픽업 예약 날짜", example = "2023-01-01T17:30")
    private LocalDateTime dateTime;

    public ReservationResponseForChat(Schedule schedule) {
        this.id = schedule.getId();
        this.dateTime = schedule.getDateTime();
    }

    @Override
    public int compareTo(ReservationResponseForChat o) {
        return this.dateTime.compareTo(o.getDateTime());
    }
}
