package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.service.Interface.StoreListService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
                .leftJoin(QStore.store.menu, QMenu.menu)
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
                .leftJoin(QStore.store.menu, QMenu.menu)
                .where(whereBuilder)
                .orderBy(QStore.store.score.desc())
                .offset((storeSearchDto.getPage() - 1) * pageSize)
                .limit(pageSize);

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
            whereBuilder.and(QMenu.menu.introduction.like("%" + storeSearchDto.getSearchType() + "%"));
        }

        if (storeSearchDto.getDessertType() != null) {
            whereBuilder.and(QMenu.menu.dessertType.eq(storeSearchDto.getDessertType()));
        }

        return whereBuilder;
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
