package com.dessert.gallery.repository.ReviewBoard;

import com.dessert.gallery.entity.QReviewBoard;
import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewBoardRepositoryImpl implements ReviewBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReviewBoard> getReviewsForMap(Store store) {
        return jpaQueryFactory.select(QReviewBoard.reviewBoard).from(QReviewBoard.reviewBoard)
                .where(QReviewBoard.reviewBoard.store.eq(store))
                .orderBy(QReviewBoard.reviewBoard.createdDate.desc())
                .limit(2)
                .fetch();
    }
}
