package com.dessert.gallery.repository.Store;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final StoreRepository storeRepository;
    private final JPAQueryFactory jpaQueryFactory;

}
