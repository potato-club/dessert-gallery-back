package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum NoticeType {
    NOTICE(0, "공지사항"), EVENT(1, "이벤트"), ALL(2, "전체");
    private final int key;
    private final String type;

    public static NoticeType findWithKey(int key) {
        return Arrays.stream(values()).filter(state -> state.key == key).findAny().orElse(ALL);
    }
}
