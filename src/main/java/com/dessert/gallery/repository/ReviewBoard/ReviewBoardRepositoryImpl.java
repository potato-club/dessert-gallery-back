package com.dessert.gallery.repository.ReviewBoard;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewBoardRepositoryImpl implements ReviewBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
