package com.dessert.gallery.repository.StoreList;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.SearchType;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.BadRequestException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreListRepositoryImpl implements StoreListRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StoreListResponseDto> getStoreListWithUser(String email, StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

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
                                QStore.store.createdDate,
                                QSubscribe.subscribe.id.as("followId")
                        )
                )
                .from(QStore.store)
                .leftJoin(QUser.user)
                .on(QUser.user.email.eq(email))
                .leftJoin(QStore.store.image, QFile.file)
                .leftJoin(QStoreBoard.storeBoard)
                .on(QStoreBoard.storeBoard.store.eq(QStore.store))
                .leftJoin(QSubscribe.subscribe)
                .on(QSubscribe.subscribe.deleted.isFalse())
                .on(QSubscribe.subscribe.user.eq(QUser.user))
                .on(QSubscribe.subscribe.store.eq(QStore.store))
                .where(whereBuilder)
                .distinct()
                .orderBy(existsOrderByOption(storeSearchDto, SearchType.STORE_BOARD))
                .offset((storeSearchDto.getPage() - 1) * 20L)
                .limit(20)
                .fetch();
    }

    @Override
    public List<StoreListResponseDto> getStoreListWithGuest(StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        return jpaQueryFactory
                .select(
                        Projections.fields(
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
                .leftJoin(QStoreBoard.storeBoard)
                .on(QStoreBoard.storeBoard.store.eq(QStore.store))
                .where(whereBuilder)
                .distinct()
                .orderBy(existsOrderByOption(storeSearchDto, SearchType.STORE_BOARD))
                .offset((storeSearchDto.getPage() - 1) * 20L)
                .limit(20)
                .fetch();
    }

    @Override
    public List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto) {
        BooleanBuilder whereBuilder = this.existsFilterOption(storeSearchDto);

        return jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                StoreReviewDto.class,
                                QStore.store.id.as("storeId"),
                                QStore.store.name.as("storeName"),
                                QStore.store.content.as("content"),
                                QStore.store.image.fileName.as("fileName"),
                                QStore.store.image.fileUrl.as("fileUrl"),
                                QStore.store.address.as("address")
                        )
                )
                .from(QStore.store)
                .innerJoin(QReviewBoard.reviewBoard)
                .on(QReviewBoard.reviewBoard.store.eq(QStore.store))
                .leftJoin(QStore.store.image, QFile.file)
                .leftJoin(QStoreBoard.storeBoard)
                .on(QStoreBoard.storeBoard.store.eq(QStore.store))
                .where(whereBuilder)
                .orderBy(existsOrderByOption(storeSearchDto, SearchType.REVIEW))
                .offset((storeSearchDto.getPage() - 1) * 20L)
                .limit(20)
                .fetch();
    }

    @Override
    public List<ReviewListDto> getRecentReviewsSubQuery(Long storeId) {
        QReviewBoard reviewBoard = QReviewBoard.reviewBoard;
        QFile file = QFile.file;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                ReviewListDto.class,
                                reviewBoard.user.nickname,
                                reviewBoard.content,
                                reviewBoard.score,
                                reviewBoard.createdDate,
                                file.fileName,
                                file.fileUrl
                        )
                )
                .from(reviewBoard)
                .leftJoin(file).on(file.reviewBoard.eq(reviewBoard))
                .where(reviewBoard.store.id.eq(storeId))
                .orderBy(reviewBoard.createdDate.desc())
                .limit(2)
                .fetch();
    }

    // OrderBy 필터링
    private OrderSpecifier<?> existsOrderByOption(StoreSearchDto storeSearchDto, SearchType searchType) {
        switch (storeSearchDto.getSortType()) {
            case RECENT:
                DateTimePath<LocalDateTime> dateTimePath;

                if (searchType.equals(SearchType.STORE_BOARD)) {
                    dateTimePath = QStore.store.createdDate;
                } else {
                    dateTimePath = QReviewBoard.reviewBoard.createdDate;
                }

                return dateTimePath.desc();

            case FOLLOWER:
                NumberExpression<Integer> sizePath = QStore.store.followers.size();

                return sizePath.desc();

            case SCORE:
                NumberPath<Double> scorePath;

                if (searchType.equals(SearchType.STORE_BOARD)) {
                    scorePath = QStore.store.score;
                } else {
                    scorePath = QReviewBoard.reviewBoard.score;
                }

                return scorePath.desc();

            default:
                throw new BadRequestException("Unexpected order type", ErrorCode.BAD_REQUEST_EXCEPTION);
        }
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
}
