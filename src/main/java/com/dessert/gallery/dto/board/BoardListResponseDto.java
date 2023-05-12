package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListResponseDto {
    @ApiModelProperty(value = "게시글 id")
    private Long boardId;
    @ApiModelProperty(value = "썸네일 이미지")
    private FileDto thumbnail;

    public BoardListResponseDto(StoreBoard board) {
        this.boardId = board.getId();
        this.thumbnail = board.getImages().isEmpty() ? null : new FileDto(board.getImages().get(0));
    }
}
