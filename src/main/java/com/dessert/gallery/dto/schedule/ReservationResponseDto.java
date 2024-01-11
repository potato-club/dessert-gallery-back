package com.dessert.gallery.dto.schedule;

import com.dessert.gallery.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ReservationResponseDto implements Comparable<ReservationResponseDto> {
    private Long id;
    private String dateTime;
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
