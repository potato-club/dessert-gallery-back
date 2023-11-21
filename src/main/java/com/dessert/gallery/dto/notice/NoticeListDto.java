package com.dessert.gallery.dto.notice;

import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.enums.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    @Schema(description = "공지글 id")
    private Long id;
    @Schema(description = "공지글 제목")
    private String title;
    @Schema(description = "공지글 내용")
    private String content;
    @Schema(description = "메인 노출 여부")
    private boolean exposed;
    @Schema(description = "공지글 타입")
    private NoticeType type;
    @Schema(description = "작성 일자")
    private String createdDate;

    public NoticeListDto(NoticeBoard notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.exposed = notice.isExposed();
        this.type = notice.getType();
        this.createdDate = parsingDateTime(notice.getCreatedDate());
    }
    private String parsingDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
