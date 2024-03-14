package com.dessert.gallery.repository.StoreBoard;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreBoardRepositoryImpl implements StoreBoardRepositoryCustom {

    private final StoreBoardRepository storeBoardRepository;
    private final JPAQueryFactory jpaQueryFactory;
}
