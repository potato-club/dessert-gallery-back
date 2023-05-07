package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private String title;
    private String content;
    private List<FileDto> images;
    private String tags;

    public BoardResponseDto(StoreBoard board) {
        this.title = board.getTitle();
        this.content = board.getContent();
        this.images = board.getImages().isEmpty() ? null : board.getImages().stream()
                .map(FileDto::new)
                .collect(Collectors.toList());
        this.tags = board.getTags();
    }
}
