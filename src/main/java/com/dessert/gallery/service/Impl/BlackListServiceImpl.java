package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.blacklist.BlackListRequestDto;
import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.UserRole;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.BlackList.BlackListRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.repository.Subscribe.SubscribeRepository;
import com.dessert.gallery.repository.User.UserRepository;
import com.dessert.gallery.service.Interface.BlackListService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlackListServiceImpl implements BlackListService {

    private final BlackListRepository blackListRepository;
    private final SubscribeRepository subscribeRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void addBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request) {
        User user = this.getUserInstance(request);
        User customer = this.commonException(user, blackListRequestDto.getUserName());

        Store store = storeRepository.findById(blackListRequestDto.getStoreId()).orElseThrow(() -> {
            throw new NotFoundException("Not Found Store", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        if (blackListRepository.existsByUserAndStoreAndDeletedIsFalse(customer, store)) {
            BlackList blackList = blackListRepository.findByStoreAndUser(store, customer);
            Subscribe subscribe = subscribeRepository.findByUserAndStore(customer, store);

            blackList.setDeleted(false);
            subscribe.setDeleted(true);
        } else {
            BlackList blackList = BlackList.builder()
                    .user(customer)
                    .store(store)
                    .deleted(false)
                    .build();

            if (subscribeRepository.existsByStoreAndUser(store, customer)) {
                Subscribe subscribe = subscribeRepository.findByUserAndStore(customer, store);
                subscribe.setDeleted(true);
            }

            blackListRepository.save(blackList);
        }
    }

    @Override
    @Transactional
    public void removeBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request) {
        User user = this.getUserInstance(request);
        User customer = this.commonException(user, blackListRequestDto.getUserName());

        Store store = storeRepository.findById(blackListRequestDto.getStoreId()).orElseThrow(() -> {
            throw new NotFoundException("Not Found Store", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        if (blackListRepository.existsByUserAndStoreAndDeletedIsFalse(customer, store)) {
            BlackList blackList = blackListRepository.findByStoreAndUser(store, customer);
            blackList.setDeleted(true);
        } else {
            throw new NotFoundException("Not found data in blacklist", ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public Page<BlackListResponseDto> getBlackList(int page, HttpServletRequest request) {
        User user = this.getUserInstance(request);

        if (user.getUserRole().equals(UserRole.USER)) {
            throw new UnAuthorizedException("Access isn't permitted on the blacklist.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (page < 1) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 20);

        Store store = storeRepository.findByUser(user);

        List<BlackListResponseDto> list = jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                BlackListResponseDto.class,
                                QBlackList.blackList.user.nickname.as("userName"),
                                QFile.file.fileName.as("fileName"),
                                QFile.file.fileUrl.as("fileUrl")
                        )
                )
                .from(QBlackList.blackList)
                .leftJoin(QFile.file).on(QFile.file.store.eq(QBlackList.blackList.store))
                .where(QBlackList.blackList.store.eq(store)
                        .and(QBlackList.blackList.deleted.isFalse()))
                .orderBy(QBlackList.blackList.modifiedDate.desc())
                .offset((page - 1) * 20L)
                .limit(20)
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(QBlackList.blackList.count())
                .from(QBlackList.blackList)
                .where(QBlackList.blackList.store.eq(store)
                        .and(QBlackList.blackList.deleted.isFalse()));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    @Override
    public void validateBlackList(Store store, User user) {
        BlackList blackList = blackListRepository.findByStoreAndUser(store, user);

        if (blackList != null && !blackList.isDeleted()) {
            throw new UnAuthorizedException("This user is blacklisted.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }
    }

    private User getUserInstance(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        return userRepository.findByEmail(email).orElseThrow();
    }

    private User commonException(User user, String username) {
        if (user.getUserRole().equals(UserRole.USER)) {
            throw new UnAuthorizedException("Do not access blacklist.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        User customer = userRepository.findByNickname(username).orElseThrow(() -> {
            throw new NotFoundException("Not found data", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        if (customer.equals(user)) {
            throw new UnAuthorizedException("Do not black myself.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        return customer;
    }
}
