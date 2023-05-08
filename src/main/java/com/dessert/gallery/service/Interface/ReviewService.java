package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ReviewService {
    List<ReviewListResponseDto> getReviewList();
    void addReview(Long storeId, ReviewRequestDto requestDto, List<MultipartFile> images, HttpServletRequest request);
}
