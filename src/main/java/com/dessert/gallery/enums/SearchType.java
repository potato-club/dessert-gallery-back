package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {

    NAME("ROLE_NAME", "가게 이름"),
    TAGS("ROLE_TAGS", "태그 검색"),
    RECENT("ROLE_RECENT", "최근 작성 순 정렬"),
    FOLLOWER("ROLE_FOLLOWER", "팔로워 순 정렬"),
    SCORE("ROLE_SCORE", "평점 순 정렬"),
    STORE_BOARD("ROLE_STORE_BOARD", "가게 게시글 기준 정렬"),
    REVIEW("ROLE_REVIEW", "가게 후기글 기준 정렬");

    private final String key;
    private final String title;
}
