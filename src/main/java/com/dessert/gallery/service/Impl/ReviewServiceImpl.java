package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.review.ReviewListResponseDto;
import com.dessert.gallery.dto.review.ReviewRequestDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.exception.S3Exception;
import com.dessert.gallery.repository.ReviewBoardRepository;
import com.dessert.gallery.service.Interface.ReviewService;
import com.dessert.gallery.service.Interface.StoreService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static com.dessert.gallery.error.ErrorCode.RUNTIME_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewBoardRepository reviewRepository;
    private final StoreService storeService;
    private final UserService userService;
    private final S3Service s3Service;

    @Override
    public Page<ReviewListResponseDto> getStoreReviews(Long storeId, int page) {
        PageRequest request = PageRequest.of(page - 1, 4,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Store store = storeService.getStore(storeId);
        Page<ReviewBoard> reviews = reviewRepository.findAllByStore(request, store);

        return reviews.map(ReviewListResponseDto::new);
    }

    @Override
    public void addReview(Long storeId, ReviewRequestDto requestDto,
                          List<MultipartFile> images, HttpServletRequest request) {
        Store store = storeService.getStore(storeId);
        User user = userService.findUserByToken(request);
        ReviewBoard review = new ReviewBoard(requestDto, store, user);
        store.setScore(getAvgScore(store, requestDto.getScore()));
        ReviewBoard saveReview = reviewRepository.save(review);
        if(images != null) {
            List<File> files = saveImage(images, saveReview);
            saveReview.setImages(files);
        }
    }

    private Double getAvgScore(Store store, double score) {
        Long reviewCount = reviewRepository.countByStore(store);
        double scoreSum = store.getScore() * reviewCount + score;
        return Math.round((scoreSum / (reviewCount + 1)) * 10) / 10.0;
    }

    private List<File> saveImage(List<MultipartFile> images, ReviewBoard review) {
        try {
            return s3Service.uploadImages(images, review);
        } catch (IOException e) {
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }
}
