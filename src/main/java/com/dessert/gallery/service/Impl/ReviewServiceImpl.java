package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.review.MyReviewListDto;
import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.ReviewBoardRepository;
import com.dessert.gallery.service.Interface.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.dessert.gallery.error.ErrorCode.NOT_ALLOW_WRITE_EXCEPTION;
import static com.dessert.gallery.error.ErrorCode.NOT_FOUND_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewBoardRepository reviewRepository;
    private final StoreService storeService;
    private final UserService userService;
    private final ImageService imageService;

    @Override
    public Page<ReviewListResponseDto> getStoreReviews(Long storeId, int page) {
        PageRequest request = PageRequest.of(page - 1, 4,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Store store = storeService.getStore(storeId);
        Page<ReviewBoard> reviews = reviewRepository.findAllByStore(request, store);

        return reviews.map(ReviewListResponseDto::new);
    }

    @Override
    public Slice<MyReviewListDto> getReviewListByUser(int page, int month, HttpServletRequest request) {
        PageRequest paging = PageRequest.of(page - 1, 10,
                Sort.by(Sort.Direction.DESC, "createdDate"));

        User user = userService.findUserByToken(request);
        Slice<ReviewBoard> reviews;

        if (month != 0) {
            LocalDateTime now = LocalDateTime.now();
            now = now.minusMonths(month);
            reviews = reviewRepository.findAllByUserAndCreatedDateGreaterThanEqual(paging, user, now);
        } else {
            reviews = reviewRepository.findAllByUser(paging, user);
        }

        return reviews.map(MyReviewListDto::new);
    }

    @Override
    public void addReview(Long storeId, ReviewRequestDto requestDto,
                          List<MultipartFile> images, HttpServletRequest request) throws IOException {
        Store store = storeService.getStore(storeId);
        User user = userService.findUserByToken(request);
        ReviewBoard review = new ReviewBoard(requestDto, store, user);
        ReviewBoard saveReview = reviewRepository.save(review);
        if (images != null) {
            List<File> files = imageService.uploadImages(images, review);
            saveReview.updateImages(files);
        }
        addScore(store, requestDto.getScore());
    }

    @Override
    public void updateReview(Long reviewId, ReviewRequestDto updateDto,
                             List<MultipartFile> images,
                             List<FileRequestDto> requestDto, HttpServletRequest request) throws IOException {
        ReviewBoard review = reviewRepository.findById(reviewId).orElse(null);
        User reviewUser = userService.findUserByToken(request);
        if (review == null) throw new NotFoundException("존재하지 않는 리뷰", NOT_FOUND_EXCEPTION);
        if (reviewUser != review.getUser()) throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        double scoreGap = review.getScore() - updateDto.getScore();
        if (images != null) {
            List<File> files = imageService.updateImages(review, images, requestDto);
            review.updateImages(files);
        }
        review.updateReview(updateDto);
        updateScore(review.getStore(), scoreGap);
    }

    @Override
    public void removeReview(Long reviewId, HttpServletRequest request) {
        ReviewBoard review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) throw new NotFoundException("존재하지 않는 리뷰", NOT_FOUND_EXCEPTION);
        Store store = review.getStore();
        deleteScore(store, review.getScore());
        reviewRepository.delete(review);
    }

    private void addScore(Store store, double score) { // 리뷰 카운트 + 1 로 계산
        Long reviewCount = reviewRepository.countByStore(store);
        double scoreSum = store.getScore() * reviewCount + score;
        store.updateScore(Math.round((scoreSum / (reviewCount + 1)) * 10) / 10.0);
    }

    private void updateScore(Store store, double score) { // 리뷰 카운트 그대로 계산 / 점수는 원래점수와 새로운점수 차이 받기
        Long reviewCount = reviewRepository.countByStore(store);
        double scoreSum = store.getScore() * reviewCount + score;
        store.updateScore(Math.round((scoreSum / reviewCount) * 10) / 10.0);
    }

    private void deleteScore(Store store, double score) { // 리뷰 카운트 - 1 로 계산 / 점수는 원래 점수 빼버리기
        Long reviewCount = reviewRepository.countByStore(store);
        double scoreSum = store.getScore() * reviewCount - score;
        store.updateScore(Math.round((scoreSum / reviewCount - 1) * 10) / 10.0);
    }
}
