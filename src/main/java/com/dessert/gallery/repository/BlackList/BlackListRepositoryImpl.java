package com.dessert.gallery.repository.BlackList;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlackListRepositoryImpl implements BlackListRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
}
