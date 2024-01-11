package com.dessert.gallery.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationRequestDto {
    @Schema(description = "픽업 예약 시간", example = "2023-01-01THH:mm", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    @Schema(description = "예약 유저 닉네임")
    private String client;
}
