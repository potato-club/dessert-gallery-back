package com.dessert.gallery.dto.review;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.ReviewBoard;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewListResponseDto {
    @ApiModelProperty(value = "리뷰 작성자 닉네임")
    private String userName;
    @ApiModelProperty(value = "리뷰 내용")
    private String content;
    @ApiModelProperty(value = "리뷰 점수")
    private Double score;
    @ApiModelProperty(value = "리뷰 이미지 파일")
    private List<FileDto> images;
    @ApiModelProperty(value = "리뷰 작성 일자", example = "2023-05-01")
    private String createDate;

    public ReviewListResponseDto(ReviewBoard review) {
        this.userName = review.getUser().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
        this.images = review.getImages().isEmpty() ? null : review.getImages()
                .stream().map(FileDto::new)
                .collect(Collectors.toList());
        this.createDate = review.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
