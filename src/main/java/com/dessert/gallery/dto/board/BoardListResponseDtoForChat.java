package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.StoreBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BoardListResponseDtoForChat {
    @Schema(description = "게시글 id")
    private Long id;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 이미지 썸네일")
    private FileDto thumbnail;

    public BoardListResponseDtoForChat(StoreBoard board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.thumbnail = board.getImages().isEmpty() ? null : new FileDto(board.getImages().get(0));
    }
}
