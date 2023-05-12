package com.dessert.gallery.dto.review;

import com.dessert.gallery.entity.ReviewBoard;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ReviewBoardResponseDto {
    @ApiModelProperty(value = "리뷰 작성자 닉네임")
    private String userName;
    @ApiModelProperty(value = "리뷰 내용")
    private String content;
    @ApiModelProperty(value = "리뷰 점수")
    private int score;
    @ApiModelProperty(value = "리뷰 작성 일자")
    private String createDate;

    public ReviewBoardResponseDto(ReviewBoard review) {
        this.userName = review.getUser().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
        this.createDate = review.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
