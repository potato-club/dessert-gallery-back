package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ScheduleType {
    RESERVATION(1, "픽업 예약"), HOLIDAY(2, "가게 휴무일"), EVENT(3, "가게 이벤트");
    private final int key;
    private final String type;
    public static ScheduleType findWithKey(int key) {
        return Arrays.stream(values()).filter(state -> state.key == key).findAny().orElseThrow();
    }
}
