package com.dessert.gallery.repository.Bookmark;

import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.QBookmark;
import com.dessert.gallery.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Bookmark> findBookmarkByUser(User user, Pageable pageable) {
        return jpaQueryFactory.select(QBookmark.bookmark).from(QBookmark.bookmark)
                .where(QBookmark.bookmark.user.eq(user))
                .orderBy(QBookmark.bookmark.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }
}
