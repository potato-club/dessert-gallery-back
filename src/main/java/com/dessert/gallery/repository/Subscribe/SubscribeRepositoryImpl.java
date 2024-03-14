package com.dessert.gallery.repository.Subscribe;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import com.dessert.gallery.entity.QBlackList;
import com.dessert.gallery.entity.QFile;
import com.dessert.gallery.entity.QSubscribe;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FollowResponseDto> findDistinctFollowResponseDtoByEmail(int page, String email) {
        return jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                FollowResponseDto.class,
                                QSubscribe.subscribe.store.id.as("storeId"),
                                QSubscribe.subscribe.store.name.as("storeName"),
                                QFile.file.fileName.as("fileName"),
                                QFile.file.fileUrl.as("fileUrl")
                        )
                )
                .from(QSubscribe.subscribe)
                .leftJoin(QFile.file).on(QFile.file.store.eq(QSubscribe.subscribe.store))
                .leftJoin(QBlackList.blackList).on(QBlackList.blackList.user.eq(QSubscribe.subscribe.user)
                        .and(QBlackList.blackList.store.eq(QSubscribe.subscribe.store)))
                .where(QSubscribe.subscribe.user.email.eq(email)
                        .and(QSubscribe.subscribe.deleted.isFalse())
                        .and(QBlackList.blackList.id.isNull().or(QBlackList.blackList.deleted.isTrue())))
                .orderBy(QSubscribe.subscribe.modifiedDate.desc())
                .offset((page - 1) * 20L)
                .limit(20)
                .fetch();
    }

    @Override
    public JPAQuery<Long> countByUserEmailAndDeletedIsFalseAndNothingBlackList(String email) {
        return jpaQueryFactory
                .select(QSubscribe.subscribe.count())
                .from(QSubscribe.subscribe)
                .leftJoin(QBlackList.blackList).on(QBlackList.blackList.user.eq(QSubscribe.subscribe.user)
                        .and(QBlackList.blackList.store.eq(QSubscribe.subscribe.store)))
                .where(QSubscribe.subscribe.user.email.eq(email)
                        .and(QSubscribe.subscribe.deleted.isFalse())
                        .and(QBlackList.blackList.id.isNull().or(QBlackList.blackList.deleted.isTrue())));
    }
}
