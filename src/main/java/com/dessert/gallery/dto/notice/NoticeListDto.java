package com.dessert.gallery.dto.notice;

import com.dessert.gallery.entity.NoticeBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    private Long id;
    private String title;

    public NoticeListDto(NoticeBoard notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
    }
}
