package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.review.*;
import com.dessert.gallery.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface ReviewService {
    Page<ReviewListResponseDto> getStoreReviews(Long storeId, int page);
    List<ReviewResponseDtoForMap> getReviewsForMap(Store store);
    Slice<MyReviewListDto> getReviewListByUser(int page, int month, HttpServletRequest request);
    void addReview(Long storeId, ReviewRequestDto requestDto, List<MultipartFile> images, HttpServletRequest request) throws IOException;
    void updateReview(Long reviewId, ReviewUpdateDto updateDto,
                      List<MultipartFile> images, HttpServletRequest request) throws IOException;
    void removeReview(Long reviewId, HttpServletRequest request);
}
