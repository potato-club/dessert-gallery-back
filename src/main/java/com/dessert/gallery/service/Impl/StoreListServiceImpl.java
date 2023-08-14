package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.entity.*;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreListServiceImpl implements StoreListService {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StoreListResponseDto> getStoreList(StoreSearchDto storeSearchDto) {

        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        JPAQuery<StoreListResponseDto> query = jpaQueryFactory
                .select(
                        Projections.constructor(
                            StoreListResponseDto.class,
                            QStore.store.id,
                            QStore.store.name,
                            QStore.store.content,
                            QStore.store.address,
                            QFile.file.fileName,
                            QFile.file.fileUrl,
                            QStore.store.score
                        )
                )
                .from(QStore.store)
                .leftJoin(QStore.store.image, QFile.file)
                .leftJoin(QStoreBoard.storeBoard).on(QStoreBoard.storeBoard.store.eq(QStore.store))
                .where(whereBuilder)
                .distinct()
                .orderBy(existsOrderByOption(storeSearchDto))
                .offset((storeSearchDto.getPage() - 1) * 20L)
                .limit(20);

        return query.fetch();
    }

    @Override
    public List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        JPAQuery<StoreReviewDto> query = jpaQueryFactory
                .select(
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
                .distinct()
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
            for (String option : storeSearchDto.getSearchType()) {
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
