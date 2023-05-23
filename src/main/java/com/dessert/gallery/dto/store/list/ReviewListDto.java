package com.dessert.gallery.dto.store.list;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewListDto {

    @ApiModelProperty(value = "리뷰 작성자 닉네임")
    private String nickname;

    @ApiModelProperty(value = "리뷰 내용")
    private String content;

    @ApiModelProperty(value = "리뷰 점수")
    private Double score;

    @ApiModelProperty(value = "리뷰 작성 일자")
    private LocalDateTime createDate;

}
