package com.dessert.gallery.dto.notice;

import com.dessert.gallery.entity.NoticeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {
    @Schema(description = "공지글 제목")
    private String title;
    @Schema(description = "공지글 내용")
    private String content;
    @Schema(description = "메인 노출 여부 (false / true)")
    private boolean exposed;
    @Schema(description = "공지글 타입 (0 / 1), 0: 공지사항 / 1: 이벤트")
    private int typeKey;

    public NoticeRequestDto(NoticeBoard notice) {
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.exposed = notice.isExposed();
        this.typeKey = notice.getType().getKey();
    }
}
