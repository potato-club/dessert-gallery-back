package com.dessert.gallery.repository.File;

import com.dessert.gallery.dto.user.response.UserProfileResponseDto;
import com.dessert.gallery.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public UserProfileResponseDto getUserProfileAsUser(User user) {
        return jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                UserProfileResponseDto.class,
                                QUser.user.nickname,
                                QUser.user.loginType,
                                QUser.user.userRole,
                                QStore.store.id.as("storeId"),
                                QFile.file.fileName,
                                QFile.file.fileUrl
                        )
                )
                .from(QUser.user)
                .leftJoin(QStore.store).on(QStore.store.user.eq(QUser.user))
                .leftJoin(QFile.file).on(QFile.file.user.eq(QUser.user))
                .where(QUser.user.eq(user))
                .fetchOne();
    }

    @Override
    public UserProfileResponseDto getUserProfileAsManager(User user, Store store) {
        return jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                UserProfileResponseDto.class,
                                QUser.user.nickname,
                                QUser.user.loginType,
                                QUser.user.userRole,
                                QStore.store.id.as("storeId"),
                                QFile.file.fileName,
                                QFile.file.fileUrl
                        )
                )
                .from(QUser.user)
                .leftJoin(QStore.store).on(QStore.store.user.eq(user))
                .leftJoin(QFile.file).on(QFile.file.user.eq(user))
                .where(QUser.user.eq(user))
                .fetchOne();
    }
}
