package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {

    NAME("ROLE_NAME", "가게 이름"),
    TAGS("ROLE_TAGS", "태그 검색");

    private final String key;
    private final String title;
}
