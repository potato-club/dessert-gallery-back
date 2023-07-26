package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListResponseDto {
    @Schema(description = "게시글 id")
    private Long boardId;
    @Schema(description = "썸네일 이미지")
    private FileDto thumbnail;

    public BoardListResponseDto(StoreBoard board) {
        this.boardId = board.getId();
        this.thumbnail = board.getImages().isEmpty() ? null : new FileDto(board.getImages().get(0));
    }
}
