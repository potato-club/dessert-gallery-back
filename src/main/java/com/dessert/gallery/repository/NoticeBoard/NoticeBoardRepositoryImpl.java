package com.dessert.gallery.repository.NoticeBoard;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeBoardRepositoryImpl implements NoticeBoardRepositoryCustom {

    private final NoticeBoardRepository noticeBoardRepository;
    private final JPAQueryFactory jpaQueryFactory;

}
