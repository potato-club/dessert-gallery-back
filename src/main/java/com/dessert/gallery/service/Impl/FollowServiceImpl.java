package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.repository.SubscribeRepository;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.FollowService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final JPAQueryFactory jpaQueryFactory;
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void addStoreFollowing(Long storeId, HttpServletRequest request) {
        String email = this.getUserEmail(request);

        User user = userRepository.findByEmail(email).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new UnAuthorizedException("Not Found", ErrorCode.ACCESS_DENIED_EXCEPTION);
        });

        if (subscribeRepository.existsByStoreAndUserAndDeletedIsTrue(store, user)) {
            Subscribe subscribe = subscribeRepository.findByUserAndStore(user, store);
            subscribe.setDeleted(true);
            return;
        }

        Subscribe subscribe = Subscribe.builder()
                .user(user)
                .store(store)
                .deleted(false)
                .build();

        subscribeRepository.save(subscribe);
    }

    @Override
    public void removeStoreFollowing(Long storeId, HttpServletRequest request) {
        String email = this.getUserEmail(request);
        UserRole userRole = jwtTokenProvider.getRoles(email);

        switch (userRole) {
            case USER: case ADMIN:
                User user = userRepository.findByEmail(email).orElseThrow();
                Subscribe subUser = subscribeRepository.findByUser(user);
                subUser.setDeleted(true);
            case MANAGER:
                Subscribe subStore = subscribeRepository.findByStoreId(storeId);
                subStore.setDeleted(true);
        }
    }

    @Override
    public List<FollowResponseDto> getFollowingList(int page, HttpServletRequest request) {

        String email = this.getUserEmail(request);
        UserRole userRole = jwtTokenProvider.getRoles(email);

        BooleanBuilder whereBuilder = new BooleanBuilder();

        switch (userRole) {
            case USER:
                whereBuilder.and(QSubscribe.subscribe.user.email.eq(email));
            case MANAGER:
                whereBuilder.and(QSubscribe.subscribe.store.user.email.eq(email));
            case ADMIN:
                whereBuilder.and(QSubscribe.subscribe.user.email.eq(email));
        }

        JPAQuery<FollowResponseDto> query = jpaQueryFactory
                .select(
                        Projections.constructor(
                                FollowResponseDto.class,
                                QStore.store.name.as("storeName"),
                                QUser.user.nickname.as("nickname"),
                                QFile.file.fileName.as("fileName"),
                                QFile.file.fileUrl.as("fileUrl")
                        )
                )
                .from(QSubscribe.subscribe)
                .leftJoin(QStore.store).on(QStore.store.user.email.eq(email))
                .leftJoin(QUser.user).on(QUser.user.email.eq(email))
                .leftJoin(QFile.file).on(QFile.file.store.user.email.eq(email)
                        .or(QFile.file.user.email.eq(email)))
                .where(whereBuilder.and(QSubscribe.subscribe.deleted.isFalse()))
                .orderBy(QSubscribe.subscribe.modifiedDate.desc())
                .offset((page - 1) * 20L)
                .limit(20);

        return query.fetch();
    }

    private String getUserEmail(HttpServletRequest request) {
        return jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
    }
}
