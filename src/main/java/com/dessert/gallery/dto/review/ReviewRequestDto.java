package com.dessert.gallery.dto.review;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String content;
    private int score;
}
