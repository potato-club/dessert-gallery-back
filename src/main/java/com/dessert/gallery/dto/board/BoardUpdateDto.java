package com.dessert.gallery.dto.board;

import com.dessert.gallery.dto.file.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardUpdateDto {
    @Schema(description = "게시글 제목")
    @NotNull(message = "제목을 작성해주세요.")
    private String title;

    @Schema(description = "게시글 내용")
    @NotNull(message = "내용을 작성해주세요.")
    private String content;

    @Schema(description = "게시글 해시태그 (예시 - \"#태그1#태그2#태그3\")")
    @NotBlank(message = "해시태그 입력값이 적절하지 않습니다.")
    @Pattern(regexp = "^(#(?:[가-힣a-zA-Z0-9]+))*$", message = "해시태그를 올바르게 입력해주세요.")
    private String tags;

    @Schema(description = "원본 중 삭제할 이미지 리스트")
    private List<FileDto> deleteFiles;
}
