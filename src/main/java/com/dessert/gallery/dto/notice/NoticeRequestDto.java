package com.dessert.gallery.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {
    @Schema(description = "공지글 내용")
    private String content;
    @Schema(description = "공지글 타입 (0 / 1), 0: 공지사항 / 1: 이벤트")
    private int typeKey;
}
