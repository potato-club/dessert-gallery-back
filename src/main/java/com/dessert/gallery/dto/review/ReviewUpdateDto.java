package com.dessert.gallery.dto.review;

import com.dessert.gallery.dto.file.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewUpdateDto {
    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 점수")
    private Double score;

    @Schema(description = "남길 이미지 리스트")
    private List<FileDto> nonDeleteFiles;
}
