package com.dessert.gallery.dto.notice;

import com.dessert.gallery.dto.file.FileResponseDto;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.enums.NoticeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class NoticeResponseDto {
    private String title;
    private String content;
    private NoticeType type;
    private List<FileResponseDto> images;

    public NoticeResponseDto(NoticeBoard noticeBoard) {
        this.title = noticeBoard.getTitle();
        this.content = noticeBoard.getContent();
        this.type = noticeBoard.getType();
        this.images = noticeBoard.getImages().isEmpty() ? null : noticeBoard.getImages().stream()
                .map(FileResponseDto::new)
                .collect(Collectors.toList());
    }
}
