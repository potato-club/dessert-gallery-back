package com.dessert.gallery.repository.NoticeBoard;

import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.QNoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.enums.NoticeType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeBoardRepositoryImpl implements NoticeBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<NoticeBoard> findNoticesForMap(Store store) {
        BooleanBuilder whereQuery = new BooleanBuilder();
        whereQuery.and(QNoticeBoard.noticeBoard.deleted.isFalse()); // 삭제 안된 공지
        whereQuery.and(QNoticeBoard.noticeBoard.exposed.isTrue()); // 노출 된 공지

        return jpaQueryFactory.select(QNoticeBoard.noticeBoard).from(QNoticeBoard.noticeBoard)
                .where(whereQuery.and(QNoticeBoard.noticeBoard.store.eq(store)))
                .orderBy(QNoticeBoard.noticeBoard.createdDate.desc())
                .limit(2).fetch();
    }

    @Override
    public List<NoticeBoard> findNoticesByStore(NoticeType type, String keyword, Long last,
                                                   Store store, Pageable pageable) {
        BooleanBuilder whereQuery = new BooleanBuilder();
        whereQuery.and(QNoticeBoard.noticeBoard.deleted.isFalse()); // 삭제 안된 공지

        if (type != NoticeType.ALL)
            whereQuery.and(QNoticeBoard.noticeBoard.type.eq(type));
        if (keyword != null)
            whereQuery.and(QNoticeBoard.noticeBoard.title.contains(keyword)); // 키워드 검색 조건
        if (last != null)
            whereQuery.and(QNoticeBoard.noticeBoard.id.lt(last)); // no-offset 을 위한 마지막 id 값 확인


        return jpaQueryFactory
                .select(QNoticeBoard.noticeBoard).from(QNoticeBoard.noticeBoard)
                .where(whereQuery.and(QNoticeBoard.noticeBoard.store.eq(store)))
                .orderBy(QNoticeBoard.noticeBoard.createdDate.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }
}
