package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.BlackListRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.repository.SubscribeRepository;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.FollowService;
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
    private final BlackListRepository blackListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addStoreFollowing(Long storeId, HttpServletRequest request) {
        String email = this.getUserEmail(request);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getUserRole().equals(UserRole.MANAGER)) {
            throw new UnAuthorizedException("Do not subscribe store.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new UnAuthorizedException("Not Found data", ErrorCode.ACCESS_DENIED_EXCEPTION);
        });

        if (blackListRepository.existsByUserAndStore(user, store)) {
            throw new UnAuthorizedException("This user is blacklisted.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (store.getUser().equals(user)) {
            throw new UnAuthorizedException("Do not subscribe same user.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (subscribeRepository.existsByStoreAndUser(store, user)) {
            Subscribe subscribe = subscribeRepository.findByUserAndStore(user, store);
            subscribe.setDeleted(false);
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
    public void removeFollowing(Long storeId, HttpServletRequest request) {
        String email = this.getUserEmail(request);
        UserRole userRole = jwtTokenProvider.getRoles(email);

        switch (userRole) {
            case USER: case ADMIN:
                User user = userRepository.findByEmail(email).orElseThrow();
                Subscribe subUser = subscribeRepository.findByUser(user);
                if (subUser == null) {
                    throw new NotFoundException("No Subscribe Data", ErrorCode.NOT_FOUND_EXCEPTION);
                }

                subUser.setDeleted(true);
            case MANAGER:
                throw new UnAuthorizedException("Access isn't permitted on the unfollowing.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }
    }

    @Override
    public List<FollowResponseDto> getFollowingList(int page, HttpServletRequest request) {

        String email = this.getUserEmail(request);
        UserRole userRole = jwtTokenProvider.getRoles(email);

        if (userRole.equals(UserRole.MANAGER)) {
            throw new UnAuthorizedException("Access isn't permitted on the following.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        JPAQuery<FollowResponseDto> query = jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                FollowResponseDto.class,
                                QSubscribe.subscribe.store.name.as("storeName"),
                                QFile.file.fileName.as("fileName"),
                                QFile.file.fileUrl.as("fileUrl")
                        )
                )
                .from(QSubscribe.subscribe)
                .leftJoin(QFile.file).on(QFile.file.store.eq(QSubscribe.subscribe.store))
                .where(QSubscribe.subscribe.user.email.eq(email)
                        .and(QSubscribe.subscribe.deleted.isFalse()))
                .orderBy(QSubscribe.subscribe.modifiedDate.desc())
                .offset((page - 1) * 20L)
                .limit(20);

        return query.fetch();
    }

    private String getUserEmail(HttpServletRequest request) {
        return jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
    }
}
