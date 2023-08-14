package com.dessert.gallery.dto.notice;

import com.dessert.gallery.entity.NoticeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    @Schema(description = "공지글 id")
    private Long id;
    @Schema(description = "공지글 제목")
    private String title;

    public NoticeListDto(NoticeBoard notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
    }
}
