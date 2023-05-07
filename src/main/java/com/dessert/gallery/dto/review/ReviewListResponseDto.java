package com.dessert.gallery.dto.review;

import com.dessert.gallery.dto.store.StoreReviewDto;
import com.dessert.gallery.entity.ReviewBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewListResponseDto {
    private StoreReviewDto storeInfo;
    private String userName;
    private String content;
    private int score;
    private int likeCount;

    public ReviewListResponseDto(ReviewBoard review) {
        this.storeInfo = new StoreReviewDto(review.getStore());
        this.userName = review.getUser().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
        this.likeCount = review.getLikeCount();
    }
}
