package com.dessert.gallery.repository.StoreList;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreListRepositoryImpl implements StoreListRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
}
