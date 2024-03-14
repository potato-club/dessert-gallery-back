package com.dessert.gallery.repository.Memo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemoRepositoryImpl implements MemoRepositoryCustom {

    private final MemoRepository memoRepository;
    private final JPAQueryFactory jpaQueryFactory;

}
