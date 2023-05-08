package com.dessert.gallery.controller;

import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.service.Interface.ReviewService;
import io.swagger.annotations.Api;
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
@Api(tags = {"Review Board Controller"})
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<List<ReviewListResponseDto>> getReviewList() {
        List<ReviewListResponseDto> reviewListDto = reviewService.getReviewList();
        return ResponseEntity.ok(reviewListDto);
    }

    @PostMapping("/stores/{storeId}")
    public ResponseEntity<String> addReview(@PathVariable(name = "storeId") Long storeId,
                                            @RequestPart ReviewRequestDto requestDto,
                                            @RequestPart(required = false) List<MultipartFile> images,
                                            HttpServletRequest request) {
        reviewService.addReview(storeId, requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }
}
