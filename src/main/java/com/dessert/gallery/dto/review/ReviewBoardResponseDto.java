package com.dessert.gallery.dto.review;

import com.dessert.gallery.entity.ReviewBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ReviewBoardResponseDto {
    private String userName;
    private String content;
    private int score;
    private String createDate;

    public ReviewBoardResponseDto(ReviewBoard review) {
        this.userName = review.getUser().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
        this.createDate = review.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
