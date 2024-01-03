package com.dessert.gallery.dto.calendar;

import com.dessert.gallery.dto.memo.MemoResponseDto;
import com.dessert.gallery.dto.schedule.ScheduleResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CalendarOwnerResponseDto {
    @Schema(description = "캘린더 해당 연도")
    private int year;

    @Schema(description = "캘린더 해당 월")
    private int month;

    @Schema(description = "연월에 해당하는 스케줄 리스트")
    private List<ScheduleResponseDto> scheduleList;

    @Schema(description = "연월에 해당하는 메모 리스트")
    private List<MemoResponseDto> memoList;

    @Schema(description = "금일 가게 휴무 여부")
    private boolean holiday;

    public CalendarOwnerResponseDto(CalendarResponseDto responseDto,
                                    List<MemoResponseDto> memoList,
                                    boolean holiday) {
        this.year = responseDto.getYear();
        this.month = responseDto.getMonth();
        this.scheduleList = responseDto.getScheduleList();
        this.memoList = memoList.isEmpty() ? null : memoList;
        this.holiday = holiday;
    }
}
