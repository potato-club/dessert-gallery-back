package com.dessert.gallery.dto.notice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {
    @ApiModelProperty(value = "공지글 제목")
    private String title;
    @ApiModelProperty(value = "공지글 내용")
    private String content;
    @ApiModelProperty(value = "공지글 타입 (0 / 1)", example = "0: 공지사항 / 1: 이벤트")
    private int typeKey;
}
