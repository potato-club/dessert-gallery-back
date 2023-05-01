package com.dessert.gallery.dto.board;

import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.StoreBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private String title;
    private String content;
    private List<File> images;
    private String tags;

    public BoardResponseDto(StoreBoard board) {
        this.title = board.getTitle();
        this.content = board.getContent();
        this.images = board.getFile();
        this.tags = board.getTags();
    }
}
