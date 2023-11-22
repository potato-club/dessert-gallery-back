package com.dessert.gallery.controller;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.review.MyReviewListDto;
import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.service.Interface.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Board Controller", description = "리뷰 게시판 API")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "가게 리뷰 조회")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<ReviewListResponseDto>> getStoreReviews(@PathVariable(name = "storeId") Long storeId,
                                                                       @RequestParam(value = "page", defaultValue = "1") int page) {
        Page<ReviewListResponseDto> reviewList = reviewService.getStoreReviews(storeId, page);
        return ResponseEntity.ok(reviewList);
    }

    @Operation(summary = "본인 리뷰 조회")
    @GetMapping("/mine")
    public ResponseEntity<Slice<MyReviewListDto>> getMyReviewList(@RequestParam(value = "p", defaultValue = "1") int page,
                                                                 @RequestParam(value = "m", defaultValue = "1") int month,
                                                                 HttpServletRequest request) {
        Slice<MyReviewListDto> reviewList = reviewService.getReviewListByUser(page, month, request);
        return ResponseEntity.ok(reviewList);
    }

    @Operation(summary = "가게에 대한 리뷰 등록")
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<String> addReview(@PathVariable(name = "storeId") Long storeId,
                                            @RequestPart ReviewRequestDto requestDto,
                                            @RequestPart(required = false) List<MultipartFile> images,
                                            HttpServletRequest request) throws IOException {
        reviewService.addReview(storeId, requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable(name = "reviewId") Long reviewId,
                                               @RequestPart ReviewRequestDto updateDto,
                                               @RequestPart(required = false) List<MultipartFile> images,
                                               @RequestPart List<FileRequestDto> requestDto,
                                               HttpServletRequest request) throws IOException {
        reviewService.updateReview(reviewId, updateDto, images, requestDto, request);
        return ResponseEntity.ok("수정 완료");
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> removeReview(@PathVariable(name = "reviewId") Long reviewId,
                                               HttpServletRequest request) {
        reviewService.removeReview(reviewId, request);
        return ResponseEntity.ok("삭제 완료");
    }
}
