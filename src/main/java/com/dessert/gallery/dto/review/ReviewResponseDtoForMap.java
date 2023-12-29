package com.dessert.gallery.dto.review;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.ReviewBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ReviewResponseDtoForMap {
    @Schema(description = "리뷰 작성자 닉네임")
    private String userName;
    @Schema(description = "리뷰 내용")
    private String content;
    @Schema(description = "리뷰 점수")
    private Double score;
    @Schema(description = "리뷰 이미지 파일")
    private FileDto image;
    @Schema(description = "리뷰 작성 일자", example = "2023-05-01")
    private String createDate;

    public ReviewResponseDtoForMap(ReviewBoard review) {
        this.userName = review.getUser().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
        this.image = CollectionUtils.isEmpty(review.getImages()) ? null : new FileDto(review.getImages().get(0));
        this.createDate = review.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
