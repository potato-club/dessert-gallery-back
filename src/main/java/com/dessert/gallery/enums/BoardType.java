package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {

    NOTICE_BOARD("ROLE_NOTICE_BOARD", "공지 게시판"),
    REVIEW_BOARD("ROLE_REVIEW_BOARD", "리뷰 게시판"),
    STORE_BOARD("ROLE_STORE_BOARD", "가게 게시판");

    private final String key;
    private final String title;
}
