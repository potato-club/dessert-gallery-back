package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.review.MyReviewListDto;
import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface ReviewService {
    Page<ReviewListResponseDto> getStoreReviews(Long storeId, int page);
    Slice<MyReviewListDto> getReviewListByUser(int page, int month, HttpServletRequest request);
    void addReview(Long storeId, ReviewRequestDto requestDto, List<MultipartFile> images, HttpServletRequest request) throws IOException;
    void updateReview(Long reviewId, ReviewRequestDto updateDto,
                      List<MultipartFile> images, List<FileRequestDto> requestDto, HttpServletRequest request) throws IOException;
    void removeReview(Long reviewId, HttpServletRequest request);
}
