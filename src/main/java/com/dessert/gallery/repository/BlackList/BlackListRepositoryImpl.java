package com.dessert.gallery.repository.BlackList;

import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
import com.dessert.gallery.entity.QBlackList;
import com.dessert.gallery.entity.QFile;
import com.dessert.gallery.entity.Store;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlackListRepositoryImpl implements BlackListRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BlackListResponseDto> getBlackListInStore(int page, Store store) {
        return jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                BlackListResponseDto.class,
                                QBlackList.blackList.user.nickname.as("userName"),
                                QFile.file.fileName.as("fileName"),
                                QFile.file.fileUrl.as("fileUrl")
                        )
                )
                .from(QBlackList.blackList)
                .leftJoin(QFile.file).on(QFile.file.user.eq(QBlackList.blackList.user))
                .where(QBlackList.blackList.store.eq(store)
                        .and(QBlackList.blackList.deleted.isFalse()))
                .orderBy(QBlackList.blackList.modifiedDate.desc())
                .offset((page - 1) * 20L)
                .limit(20)
                .fetch();
    }

    @Override
    public JPAQuery<Long> countByStoreAndDeletedIsFalse(Store store) {
        return jpaQueryFactory
                .select(QBlackList.blackList.count())
                .from(QBlackList.blackList)
                .where(QBlackList.blackList.store.eq(store)
                        .and(QBlackList.blackList.deleted.isFalse()));
    }
}
