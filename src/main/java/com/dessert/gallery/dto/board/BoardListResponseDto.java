package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileResponseDto;
import com.dessert.gallery.entity.StoreBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListResponseDto {
    private Long boardId;
    private FileResponseDto thumbnail;

    public BoardListResponseDto(StoreBoard board) {
        this.boardId = board.getId();
        this.thumbnail = board.getImages().isEmpty() ? null : new FileResponseDto(board.getImages().get(0));
    }
}
