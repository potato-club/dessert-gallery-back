package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.review.*;
import com.dessert.gallery.dto.store.StoreWritableReviewDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.ReviewBoard.ReviewBoardRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.service.Interface.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Override
    public Page<ReviewListResponseDto> getStoreReviews(Long storeId, int page) {
        PageRequest request = PageRequest.of(page - 1, 4,
                Sort.by(Sort.Direction.DESC, "createdDate"));

        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("검색된 가게가 없음", NOT_FOUND_EXCEPTION);
        });

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
    public List<StoreWritableReviewDto> getStoreListWritableReview(HttpServletRequest request) {
        User client = userService.findUserByToken(request);

        // store 가 같은 스케줄은 dateTime 이 제일 큰 스케줄만 가져와서 저장
        // 스케줄은 가장 최근에 픽업 완료된 순서대로 보여줌
        List<Schedule> checkedSchedules = jpaQueryFactory.select(QSchedule.schedule).from(QSchedule.schedule)
                .where(QSchedule.schedule.completed.isTrue().and(QSchedule.schedule.submitReview.isFalse())
                        .and(QSchedule.schedule.client.eq(client)))
                .groupBy(QSchedule.schedule.id, QSchedule.schedule.calendar.store)
                .having(QSchedule.schedule.dateTime.eq(
                        JPAExpressions.select(QSchedule.schedule.dateTime.max())
                                .from(QSchedule.schedule)
                                .where(QSchedule.schedule.calendar.store.eq(QSchedule.schedule.calendar.store))
                ))
                .orderBy(QSchedule.schedule.modifiedDate.desc())
                .fetch();

        return checkedSchedules.stream()
                .map(s -> new StoreWritableReviewDto(s.getCalendar().getStore()))
                .collect(Collectors.toList());
    }

    @Override
    public void addTestReview(ReviewRequestDto requestDto, List<MultipartFile> images, HttpServletRequest request) throws IOException {
        Store store = storeRepository.findById(9L).orElseThrow(() -> {
            throw new NotFoundException("테스트 가게 삭제됐음 재생성 요청", NOT_FOUND_EXCEPTION);
        });

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
    public void addReview(Long storeId, ReviewRequestDto requestDto,
                          List<MultipartFile> images, HttpServletRequest request) throws IOException {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("검색된 가게가 없음", NOT_FOUND_EXCEPTION);
        });

        User user = userService.findUserByToken(request);

        Schedule schedule = jpaQueryFactory.select(QSchedule.schedule).from(QSchedule.schedule)
                .where(QSchedule.schedule.completed.isTrue().and(QSchedule.schedule.submitReview.isFalse())
                        .and(QSchedule.schedule.client.eq(user).and(QSchedule.schedule.calendar.store.eq(store))))
                .orderBy(QSchedule.schedule.dateTime.desc())
                .fetchFirst();

        if (schedule == null) {
            throw new UnAuthorizedException("픽업 완료한 가게만 리뷰 작성 가능", NOT_ALLOW_WRITE_EXCEPTION);
        }

        ReviewBoard review = new ReviewBoard(requestDto, store, user);
        ReviewBoard saveReview = reviewRepository.save(review);

        if (images != null) {
            List<File> files = imageService.uploadImages(images, review);
            saveReview.updateImages(files);
        }

        addScore(store, requestDto.getScore());
        schedule.submitReview();
    }

    @Override
    public void updateReview(Long reviewId, ReviewUpdateDto updateDto,
                             List<MultipartFile> images, HttpServletRequest request) throws IOException {
        ReviewBoard review = validateReview(reviewId, request);
        double scoreGap = review.getScore() - updateDto.getScore();
        review.updateReview(updateDto);
        updateScore(review.getStore(), scoreGap);

        // 삭제할 파일 리스트 저장
        Set<FileDto> set = new HashSet<>(updateDto.getDeleteFiles());

        // 기존 파일 리스트에서 삭제할 파일 리스트 비교
        List<FileRequestDto> originImages = new ArrayList<>();
        List<FileDto> collect = review.getImages().stream().map(FileDto::new).collect(Collectors.toList());

        for (FileDto dto : collect) {
            // 삭제할 파일 리스트에 존재 => 삭제
            if (set.contains(dto)) {
                originImages.add(new FileRequestDto(dto, true));
                review.removeImage(dto);
            }

            // 삭제할 파일 리스트에 존재 X => 삭제 X
            else {
                originImages.add(new FileRequestDto(dto, false));
            }
        }

        // 기존 이미지 없고 업로드할 이미지 있는 경우
        if (CollectionUtils.isEmpty(originImages) && !CollectionUtils.isEmpty(images)) {
            List<File> files = imageService.uploadImages(images, review);
            review.updateImages(files);
        }

        // 기존 이미지 있는 경우 => 업데이트 진행
        if (!CollectionUtils.isEmpty(originImages)) {
            List<File> files = imageService.updateImages(review, images, originImages);
            review.updateImages(files);
        }
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
