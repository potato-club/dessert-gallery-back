package com.dessert.gallery.controller;

import com.dessert.gallery.dto.review.*;
import com.dessert.gallery.dto.store.StoreWritableReviewDto;
import com.dessert.gallery.service.Interface.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(summary = "리뷰 작성 가능한 가게 조회 API - 회원")
    @GetMapping("/writable")
    public ResponseEntity<List<StoreWritableReviewDto>> getWritableReview(HttpServletRequest request) {
        List<StoreWritableReviewDto> dtoList = reviewService.getStoreListWritableReview(request);
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "가게 리뷰 조회")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<ReviewListResponseDto>> getStoreReviews(@PathVariable(name = "storeId") Long storeId,
                                                                       @RequestParam(value = "page", defaultValue = "1") int page) {
        Page<ReviewListResponseDto> reviewList = reviewService.getStoreReviews(storeId, page);
        return ResponseEntity.ok(reviewList);
    }

    @Operation(summary = "본인 리뷰 조회 - 회원")
    @GetMapping("/mine")
    public ResponseEntity<Slice<MyReviewListDto>> getMyReviewList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @Parameter(description = "전체 리뷰 확인 - month=0")
                                                                        @RequestParam(value = "month", defaultValue = "1") int month,
                                                                  HttpServletRequest request) {
        Slice<MyReviewListDto> reviewList = reviewService.getReviewListByUser(page, month, request);
        return ResponseEntity.ok(reviewList);
    }

    @Operation(summary = "테스트 리뷰 작성 API")
    @PostMapping(value = "/test",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> addTestReview(@Parameter(description = "리뷰 정보 - ReviewRequestDto", content =
                                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                        @RequestPart ReviewRequestDto requestDto,
                                                @Parameter(description = "업로드 이미지 리스트", content =
                                                    @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                        @RequestPart(required = false) List<MultipartFile> images,
                                                HttpServletRequest request) throws IOException {
        reviewService.addTestReview(requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }


    @Operation(summary = "가게에 대한 리뷰 등록")
    @PostMapping(value = "/stores/{storeId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> addReview(@PathVariable(name = "storeId") Long storeId,
                                            @Parameter(description = "리뷰 정보 - ReviewRequestDto", content =
                                                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                    @RequestPart ReviewRequestDto requestDto,
                                            @Parameter(description = "업로드 이미지 리스트", content =
                                                @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                    @RequestPart(required = false) List<MultipartFile> images,
                                            HttpServletRequest request) throws IOException {
        reviewService.addReview(storeId, requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 완료");
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping(value = "/{reviewId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateReview(@PathVariable(name = "reviewId") Long reviewId,
                                               @Parameter(description = "수정할 리뷰 정보 - ReviewUpdateDto", content =
                                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                        @RequestPart ReviewUpdateDto updateDto,
                                               @Parameter(description = "추가할 이미지 리스트", content =
                                                    @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                        @RequestPart(required = false) List<MultipartFile> images,
                                               HttpServletRequest request) throws IOException {
        reviewService.updateReview(reviewId, updateDto, images, request);
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
