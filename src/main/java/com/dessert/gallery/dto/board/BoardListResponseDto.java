package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class BoardListResponseDto {
    @Schema(description = "게시글 id")
    private Long boardId;
    @Schema(description = "썸네일 이미지")
    private FileDto thumbnail;
    @Schema(description = "게시글 생성 날짜")
    private String createdDate;

    public BoardListResponseDto(StoreBoard board) {
        this.boardId = board.getId();
        this.thumbnail = board.getImages().isEmpty() ? null : new FileDto(board.getImages().get(0));
        this.createdDate = board.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
