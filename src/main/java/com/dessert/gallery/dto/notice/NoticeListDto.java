package com.dessert.gallery.dto.notice;

import com.dessert.gallery.entity.NoticeBoard;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    @ApiModelProperty(value = "공지글 id")
    private Long id;
    @ApiModelProperty(value = "공지글 제목")
    private String title;

    public NoticeListDto(NoticeBoard notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
    }
}
