package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.service.Interface.StoreListService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreListServiceImpl implements StoreListService {

    private final JPAQueryFactory jpaQueryFactory;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public List<StoreListResponseDto> getStoreList(StoreSearchDto storeSearchDto, HttpServletRequest request) {

        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        QUser qUser = QUser.user;

        if (accessToken != null) {
            String email = jwtTokenProvider.getUserEmail(accessToken);
            return jpaQueryFactory
                    .selectDistinct(
                            Projections.constructor(
                                    StoreListResponseDto.class,
                                    QStore.store.id,
                                    QStore.store.name,
                                    QStore.store.content,
                                    QStore.store.address,
                                    QFile.file.fileName,
                                    QFile.file.fileUrl,
                                    QStore.store.score,
                                    QStore.store.createdDate,
                                    QSubscribe.subscribe.id.as("followId"),
                                    QBookmark.bookmark.id.as("bookmarkId")
                            )
                    )
                    .from(QStore.store, QUser.user)
                    .leftJoin(QStore.store.image, QFile.file)
                    .leftJoin(QStoreBoard.storeBoard)
                        .on(QStoreBoard.storeBoard.store.eq(QStore.store))
                    .leftJoin(QSubscribe.subscribe)
                        .on(QSubscribe.subscribe.deleted.isFalse())
                        .on(QSubscribe.subscribe.user.eq(qUser).and(qUser.email.eq(email)))
                        .on(QSubscribe.subscribe.store.eq(QStore.store))
                    .leftJoin(QBookmark.bookmark)
                        .on(QBookmark.bookmark.user.eq(qUser).and(qUser.email.eq(email)))
                        .on(QBookmark.bookmark.board.store.id.eq(QStore.store.id))
                    .where(whereBuilder)
                    .orderBy(existsOrderByOption(storeSearchDto))
                    .offset((storeSearchDto.getPage() - 1) * 20L)
                    .limit(20)
                    .fetch();
        } else {
            return jpaQueryFactory
                    .select(
                            Projections.constructor(
                                    StoreListResponseDto.class,
                                    QStore.store.id,
                                    QStore.store.name,
                                    QStore.store.content,
                                    QStore.store.address,
                                    QFile.file.fileName,
                                    QFile.file.fileUrl,
                                    QStore.store.score,
                                    QStore.store.createdDate
                            )
                    )
                    .from(QStore.store)
                    .leftJoin(QStore.store.image, QFile.file)
                    .leftJoin(QStoreBoard.storeBoard).on(QStoreBoard.storeBoard.store.eq(QStore.store))
                    .where(whereBuilder)
                    .distinct()
                    .orderBy(existsOrderByOption(storeSearchDto))
                    .offset((storeSearchDto.getPage() - 1) * 20L)
                    .limit(20)
                    .fetch();
        }
    }

    @Override
    public List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto, HttpServletRequest request) {
        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        JPAQuery<StoreReviewDto> query = jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                            StoreReviewDto.class,
                            QStore.store.id.as("storeId"),
                            QStore.store.name.as("storeName"),
                            QStore.store.content.as("content"),
                            QStore.store.image.fileName.as("fileName"),
                            QStore.store.image.fileUrl.as("fileUrl")
                        )
                )
                .from(QStore.store)
                .leftJoin(QStore.store.image, QFile.file)
                .leftJoin(QStoreBoard.storeBoard).on(QStoreBoard.storeBoard.store.eq(QStore.store))
                .where(whereBuilder)
                .orderBy(existsOrderByOption(storeSearchDto))
                .offset((storeSearchDto.getPage() - 1) * 20L)
                .limit(20);

        List<StoreReviewDto> storeReviewList = query.fetch();

        for (StoreReviewDto storeReview : storeReviewList) {
            List<ReviewListDto> reviewList = getRecentReviewsSubQuery(storeReview.getStoreId());
            storeReview.setReviewList(reviewList);
        }

        return storeReviewList;
    }

    private BooleanBuilder existsFilterOption(StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (storeSearchDto.getAddress() != null) {
            whereBuilder.and(QStore.store.address.like("%" + storeSearchDto.getAddress() + "%"));
        }

        if (storeSearchDto.getSearchType() != null) {
            String[] searchTypeList = storeSearchDto.getSearchType().split("#");

            for (String option : searchTypeList) {
                whereBuilder.and(QStoreBoard.storeBoard.tags.like("%" + option + "%"));
            }
        }

        return whereBuilder;
    }

    // OrderBy 필터링
    private OrderSpecifier<?> existsOrderByOption(StoreSearchDto storeSearchDto) {
        switch (storeSearchDto.getSortType()) {
            case RECENT:
                DateTimePath<LocalDateTime> dateTimePath = QStore.store.createdDate;
                return dateTimePath.desc();
            case FOLLOWER:
                NumberExpression<Integer> sizePath = QStore.store.followers.size();
                return sizePath.desc();
            case SCORE:
                NumberPath<Double> scorePath = QStore.store.score;
                return scorePath.desc();
            default:
                throw new IllegalArgumentException("Unexpected order type: " + storeSearchDto.getSortType());
        }
    }

    private List<ReviewListDto> getRecentReviewsSubQuery(Long storeId) {
        QReviewBoard reviewBoard = QReviewBoard.reviewBoard;

        JPAQuery<ReviewListDto> query = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ReviewListDto.class,
                                reviewBoard.user.nickname,
                                reviewBoard.content,
                                reviewBoard.score,
                                reviewBoard.createdDate
                        )
                )
                .from(reviewBoard)
                .where(reviewBoard.store.id.eq(storeId))
                .orderBy(reviewBoard.createdDate.desc())
                .limit(2);

        return query.fetch();
    }
}
