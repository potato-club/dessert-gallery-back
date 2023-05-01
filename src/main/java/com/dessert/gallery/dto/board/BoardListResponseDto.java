package com.dessert.gallery.dto.board;

import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.StoreBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListResponseDto {
    private Long boardId;
    private File thumbnail;

    public BoardListResponseDto(StoreBoard board) {
        this.boardId = board.getId();
        this.thumbnail = board.getFile().isEmpty() ? null : board.getFile().get(0);
    }
}
