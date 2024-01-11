package com.dessert.gallery.dto.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleDetailResponseDto {
    private String date;
    private List<ReservationResponseDto> responseDto;
    private Long holidayId;
    private Long eventId;

    public ScheduleDetailResponseDto(LocalDateTime dateTime,
                                     List<ReservationResponseDto> reservations,
                                     Long holidayId, Long eventId) {
        this.date = parsingDateTime(dateTime);
        this.responseDto = reservations;
        this.holidayId = holidayId;
        this.eventId = eventId;
    }

    private String parsingDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
