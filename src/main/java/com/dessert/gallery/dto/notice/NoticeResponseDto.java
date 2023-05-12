package com.dessert.gallery.dto.notice;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.enums.NoticeType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class NoticeResponseDto {
    @ApiModelProperty(value = "공지글 제목")
    private String title;
    @ApiModelProperty(value = "공지글 내용")
    private String content;
    @ApiModelProperty(value = "공지글 타입", example = "NOTICE / EVENT")
    private NoticeType type;
    @ApiModelProperty(value = "공지글 이미지 파일")
    private List<FileDto> images;

    public NoticeResponseDto(NoticeBoard noticeBoard) {
        this.title = noticeBoard.getTitle();
        this.content = noticeBoard.getContent();
        this.type = noticeBoard.getType();
        this.images = noticeBoard.getImages().isEmpty() ? null : noticeBoard.getImages().stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
    }
}
