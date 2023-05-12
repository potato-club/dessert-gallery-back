package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.review.ReviewBoardResponseDto;
import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.repository.ReviewBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.ReviewService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewBoardRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @Override
    public List<ReviewBoardResponseDto> getTop2Review(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        List<ReviewBoard> top2Review = reviewRepository.findTop2ByStoreOrderByCreatedDateDesc(store);
        return top2Review.stream()
                .map(ReviewBoardResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewListResponseDto> getStoreReviews(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        List<ReviewBoard> reviews = reviewRepository.findAllByStore(store);
        List<ReviewListResponseDto> reviewListDto = reviews.stream()
                .map(ReviewListResponseDto::new)
                .collect(Collectors.toList());
        return reviewListDto;
    }

    @Override
    public void addReview(Long storeId, ReviewRequestDto requestDto,
                          List<MultipartFile> images, HttpServletRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        User user = userService.findUserByToken(request);
        ReviewBoard review = new ReviewBoard(requestDto, store, user);
        if(!images.isEmpty()) {
            List<File> files = saveImage(images, review);
            review.setImages(files);
        }
        store.setScore(getAvgScore(store, requestDto.getScore()));
        reviewRepository.save(review);
    }

    private Double getAvgScore(Store store, int score) {
        Long reviewCount = reviewRepository.countByStore(store);
        double scoreSum = store.getScore() * reviewCount + score;
        return Math.round((scoreSum / (reviewCount + 1)) * 10) / 10.0;
    }

    private List<File> saveImage(List<MultipartFile> images, ReviewBoard review) {
        try {
            return s3Service.uploadImages(images, review);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 에러");
        }
    }
}
