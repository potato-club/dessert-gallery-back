package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.service.Interface.StoreListService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreListServiceImpl implements StoreListService {

    private final JPAQueryFactory jpaQueryFactory;

    private final int pageSize = 20;

    @Override
    public List<StoreListResponseDto> getStoreList(StoreSearchDto storeSearchDto) {

        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        JPAQuery<StoreListResponseDto> query = jpaQueryFactory
                .select(
                        Projections.fields(
                            StoreListResponseDto.class,
                            QStore.store.id,
                            QStore.store.name,
                            QStore.store.content,
                            QStore.store.address,
                            QFile.file.fileName.as("fileName"),
                            QFile.file.fileUrl.as("fileUrl"),
                            QStore.store.score
                        )
                )
                .from(QStore.store)
                .innerJoin(QFile.file).on(QStore.store.id.eq(QFile.file.store.id))
                .innerJoin(QMenu.menu).on(QStore.store.id.eq(QMenu.menu.store.id))
                .where(whereBuilder)
                .orderBy(storeSearchDto.isSortType() ? QStore.store.followers.size().desc() : QStore.store.score.desc())
                .offset((storeSearchDto.getPage() - 1) * pageSize)
                .limit(pageSize);

        return query.fetch();
    }

    @Override
    public List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        JPAQuery<StoreReviewDto> query = jpaQueryFactory
                .select(
                        Projections.fields(
                            StoreReviewDto.class,
                            QStore.store.id.as("storeId"),
                            QStore.store.name.as("storeName"),
                            QStore.store.content,
                            QStore.store.image.fileName,
                            QStore.store.image.fileUrl
                        )
                )
                .from(QStore.store)
                .innerJoin(QFile.file).on(QStore.store.id.eq(QFile.file.store.id))
                .innerJoin(QMenu.menu).on(QStore.store.id.eq(QMenu.menu.store.id))
                .where(whereBuilder)
                .orderBy(QStore.store.score.desc())
                .offset((storeSearchDto.getPage() - 1) * pageSize)
                .limit(pageSize);

        List<StoreReviewDto> storeReviewList = query.fetch();

        for (StoreReviewDto storeReview : storeReviewList) {
            SubQueryExpression<ReviewListDto> subQuery = getRecentReviewsSubQuery(storeReview.getStoreId());
            List<ReviewListDto> reviewList = jpaQueryFactory
                    .select(subQuery)
                    .fetch();
            storeReview.setReviewList(reviewList);
        }

        return storeReviewList;
    }

    private BooleanBuilder existsFilterOption(StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (storeSearchDto.getAddress() != null) {
            whereBuilder.and(QStore.store.address.containsIgnoreCase(storeSearchDto.getAddress()));
        }

        if (storeSearchDto.getSearchType() != null) {
            whereBuilder.and(QMenu.menu.introduction.containsIgnoreCase(storeSearchDto.getSearchType()));
        }

        if (storeSearchDto.getDessertType() != null) {
            whereBuilder.and(QMenu.menu.dessertType.eq(storeSearchDto.getDessertType()));
        }

        return whereBuilder;
    }

    private SubQueryExpression<ReviewListDto> getRecentReviewsSubQuery(Long storeId) {
        QReviewBoard reviewBoard = QReviewBoard.reviewBoard;
        QUser user = QUser.user;

        return JPAExpressions
                .select(
                        Projections.fields(
                                ReviewListDto.class,
                                user.nickname,
                                reviewBoard.content,
                                reviewBoard.score,
                                reviewBoard.createdDate
                        )
                )
                .from(reviewBoard)
                .innerJoin(reviewBoard.user, user)
                .where(reviewBoard.store.id.eq(storeId))
                .orderBy(reviewBoard.createdDate.desc())
                .limit(2);
    }
}
