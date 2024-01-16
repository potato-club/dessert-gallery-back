package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    CHAT("ROLE_CHAT", "일반 채팅"),
    RESERVATION("ROLE_RESERVATION", "예약 확정"),
    REVIEW("ROLE_REVIEW", "리뷰 작성");

    private final String key;
    private final String title;
}
