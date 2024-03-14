package com.dessert.gallery.repository.BoardComment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCommentRepositoryImpl implements BoardCommentRepositoryCustom {

    private final BoardCommentRepository boardCommentRepository;
    private final JPAQueryFactory jpaQueryFactory;
}
