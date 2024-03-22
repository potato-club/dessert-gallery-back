package com.dessert.gallery.dto.comment;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.BoardComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    @Schema(description = "댓글 id")
    private Long id;
    @Schema(description = "유저 닉네임")
    private String nickname;
    @Schema(description = "댓글 내용")
    private String comment;
    @Schema(description = "프로필 사진 정보")
    private FileDto profile;
    @Schema(description = "작성 날짜")
    private LocalDateTime createdDate;
    @Schema(description = "댓글 작성자 여부")
    private boolean writer;

    public CommentResponseDto(BoardComment comment, boolean writer) {
        this.id = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.comment = comment.getComment();
        this.createdDate = comment.getCreatedDate();
        this.profile = comment.getUserProfile() == null ? null : new FileDto(comment.getUserProfile());
        this.writer = writer;
    }
}
