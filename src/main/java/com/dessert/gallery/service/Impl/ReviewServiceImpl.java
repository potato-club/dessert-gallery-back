package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.review.MyReviewListDto;
import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.dto.review.ReviewResponseDtoForMap;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.ReviewBoardRepository;
import com.dessert.gallery.service.Interface.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.NOT_ALLOW_WRITE_EXCEPTION;
import static com.dessert.gallery.error.ErrorCode.NOT_FOUND_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final JPAQueryFactory jpaQueryFactory;
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
    public List<ReviewResponseDtoForMap> getReviewsForMap(Store store) {
        List<ReviewBoard> reviews = jpaQueryFactory.select(QReviewBoard.reviewBoard).from(QReviewBoard.reviewBoard)
                .where(QReviewBoard.reviewBoard.store.eq(store))
                .orderBy(QReviewBoard.reviewBoard.createdDate.desc())
                .limit(2).fetch();

        return reviews.stream().map(ReviewResponseDtoForMap::new).collect(Collectors.toList());
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
                             List<FileRequestDto> requestDto,
                             HttpServletRequest request) throws IOException {
        ReviewBoard review = validateReview(reviewId, request);
        double scoreGap = review.getScore() - updateDto.getScore();
        review.updateReview(updateDto);
        updateScore(review.getStore(), scoreGap);

        // images == null 이면 빈 배열 생성
        if (images == null) images = new ArrayList<>();
        List<File> fileList;

        // requestDto 가 null 이고 images 가 있다면 이미지 업로드와 같음
        if (requestDto == null && images.size() != 0) {
            fileList = imageService.uploadImages(images, review);
        } else {
            fileList = imageService.updateImages(review, images, requestDto);
        }
        review.imageClear();
        review.updateImages(fileList);

    }

    @Override
    public void removeReview(Long reviewId, HttpServletRequest request) {
        ReviewBoard review = validateReview(reviewId, request);
        Store store = review.getStore();
        deleteScore(store, review.getScore());
        reviewRepository.delete(review);
    }

    private ReviewBoard validateReview(Long reviewId, HttpServletRequest request) {
        ReviewBoard review = reviewRepository.findById(reviewId).orElse(null);
        User reviewUser = userService.findUserByToken(request);
        if (review == null) throw new NotFoundException("존재하지 않는 리뷰", NOT_FOUND_EXCEPTION);
        if (reviewUser != review.getUser()) throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        return review;
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
