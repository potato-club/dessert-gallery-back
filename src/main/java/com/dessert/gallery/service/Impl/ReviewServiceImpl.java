package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.repository.ReviewBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.ReviewService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
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

    @Override
    public List<ReviewListResponseDto> getReviewList() {
        List<ReviewBoard> reviews = reviewRepository.findAll();
        List<ReviewListResponseDto> reviewListDto = reviews.stream()
                .map(ReviewListResponseDto::new)
                .collect(Collectors.toList());
        return reviewListDto;
    }

    @Override
    public void addReview(Long storeId, ReviewRequestDto requestDto, HttpServletRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        User user = userService.findUserByToken(request);
        ReviewBoard review = new ReviewBoard(requestDto, store, user);
        reviewRepository.save(review);
        updateStoreScore(store);
    }

    private void updateStoreScore(Store store) {
        List<ReviewBoard> reviews = reviewRepository.findByStore(store);
        int scoreSum = reviews.stream().map(ReviewBoard::getScore).mapToInt(Integer::intValue).sum();
        Double avgScore = Math.round(((double) scoreSum / reviews.size()) * 10) / 10.0;
        store.setScore(avgScore);
    }
}
