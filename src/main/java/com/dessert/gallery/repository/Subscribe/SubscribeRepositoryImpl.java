package com.dessert.gallery.repository.Subscribe;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
