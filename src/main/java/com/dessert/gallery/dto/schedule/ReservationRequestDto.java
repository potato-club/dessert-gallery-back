package com.dessert.gallery.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationRequestDto {
    @Schema(description = "픽업 예약 시간", example = "2023-01-01THH:mm", required = true)
    @NotNull(message = "예약 시간 정보는 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    @Schema(description = "예약 유저 닉네임")
    @NotBlank(message = "예약한 유저의 닉네임을 입력해주세요.")
    private String client;
}
