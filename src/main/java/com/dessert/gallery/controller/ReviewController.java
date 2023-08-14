package com.dessert.gallery.controller;

import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.service.Interface.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Board Controller", description = "리뷰 게시판 API")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "가게 리뷰 조회")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<ReviewListResponseDto>> getStoreReviews(@PathVariable(name = "storeId") Long storeId) {
        List<ReviewListResponseDto> reviewList = reviewService.getStoreReviews(storeId);
        return ResponseEntity.ok(reviewList);
    }

    @Operation(summary = "가게에 대한 리뷰 등록")
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<String> addReview(@PathVariable(name = "storeId") Long storeId,
                                            @RequestPart ReviewRequestDto requestDto,
                                            @RequestPart(required = false) List<MultipartFile> images,
                                            HttpServletRequest request) {
        reviewService.addReview(storeId, requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }
}
