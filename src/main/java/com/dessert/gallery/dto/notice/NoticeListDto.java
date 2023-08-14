package com.dessert.gallery.dto.notice;

import com.dessert.gallery.entity.NoticeBoard;
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
    @Schema(description = "공지글 내용")
    private String content;
    @Schema(description = "작성 일자")
    private String createdDate;

    public NoticeListDto(NoticeBoard notice) {
        this.id = notice.getId();
        this.content = notice.getContent();
        this.createdDate = parsingDateTime(notice.getCreatedDate());
    }
    private String parsingDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
