package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardUpdateDto {
    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 해시태그 (예시 - \"#태그1#태그2#태그3\")")
    @Pattern(regexp = "^(#(?:[가-힣a-zA-Z0-9]+))*$", message = "해시태그를 올바르게 입력해주세요.")
    private String tags;

    @Schema(description = "남길 이미지 리스트")
    private List<FileDto> nonDeleteFiles;
}
