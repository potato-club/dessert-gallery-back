package com.dessert.gallery.dto.store.list;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ReviewListDto {

    @ApiModelProperty(value = "리뷰 작성자 닉네임")
    private String nickname;

    @ApiModelProperty(value = "리뷰 내용")
    private String content;

    @ApiModelProperty(value = "리뷰 점수")
    private int score;

    @ApiModelProperty(value = "리뷰 작성 일자")
    private String createDate;

    public ReviewListDto(String nickname, String content, int score, LocalDateTime createDate) {
        this.nickname = nickname;
        this.content = content;
        this.score = score;
        this.createDate = createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
