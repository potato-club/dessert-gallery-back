package com.dessert.gallery.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {
    private String title;
    private String content;
    private int typeKey;
}
