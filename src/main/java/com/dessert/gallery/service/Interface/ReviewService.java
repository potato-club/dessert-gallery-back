package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ReviewService {
    List<ReviewListResponseDto> getReviewList();
    void addReview(Long storeId, ReviewRequestDto requestDto, HttpServletRequest request);
}
