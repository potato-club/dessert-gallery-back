package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    @Schema(description = "게시글 제목")
    private String title;
    @Schema(description = "게시글 부제목")
    private String subtitle;
    @Schema(description = "게시글 내용")
    private String content;
    @Schema(description = "게시글 이미지 파일")
    private List<FileDto> images;
    @Schema(description = "게시글 해시태그")
    private String tags;

    public BoardResponseDto(StoreBoard board) {
        this.title = board.getTitle();
        this.subtitle = board.getSubtitle();
        this.content = board.getContent();
        this.images = board.getImages().isEmpty() ? null : board.getImages().stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
        this.tags = board.getTags();
    }
}
