package com.dessert.gallery.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleDetailResponseDto {
    @Schema(description = "해당 날짜", example = "2024-01-01")
    private String date;

    @Schema(description = "픽업 예약 리스트")
    private List<ReservationResponseDto> responseDto;

    @Schema(description = "휴무 일정 id")
    private Long holidayId;

    @Schema(description = "이벤트 일정 id")
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
