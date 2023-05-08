package com.dessert.gallery.dto.review;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.ReviewBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewListResponseDto {
    private String userName;
    private String content;
    private int score;
    private List<FileDto> images;
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
