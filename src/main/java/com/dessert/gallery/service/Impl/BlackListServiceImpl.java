package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.blacklist.BlackListRequestDto;
import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
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
import com.dessert.gallery.service.Interface.BlackListService;
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
public class BlackListServiceImpl implements BlackListService {

    private final BlackListRepository blackListRepository;
    private final SubscribeRepository subscribeRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void addBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request) {
        User user = this.getUserInstance(request);
        User customer = this.commonException(user, blackListRequestDto.getUserName());

        Store store = storeRepository.findByUser(user);

        if (blackListRepository.existsByUsernameAndStore(customer.getNickname(), store)) {
            BlackList blackList = blackListRepository.findByStore(store);
            Subscribe subscribe = subscribeRepository.findByUserAndStore(user, store);

            blackList.setDeleted(false);
            subscribe.setDeleted(true);
        } else {
            BlackList blackList = BlackList.builder()
                    .username(customer.getNickname())
                    .store(store)
                    .deleted(false)
                    .build();

            if (subscribeRepository.existsByStoreAndUser(store, customer)) {
                Subscribe subscribe = subscribeRepository.findByUser(customer);
                subscribe.setDeleted(true);
            }

            blackListRepository.save(blackList);
        }
    }

    @Override
    public void removeBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request) {
        User user = this.getUserInstance(request);
        User customer = this.commonException(user, blackListRequestDto.getUserName());

        Store store = storeRepository.findByUser(user);

        if (blackListRepository.existsByUsernameAndStore(customer.getNickname(), store)) {
            BlackList blackList = blackListRepository.findByStore(store);
            blackList.setDeleted(true);
        } else {
            throw new NotFoundException("Not found data in blacklist", ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }

    @Override
    public List<BlackListResponseDto> getBlackList(int page, HttpServletRequest request) {
        User user = this.getUserInstance(request);

        if (user.getUserRole().equals(UserRole.USER)) {
            throw new UnAuthorizedException("Access isn't permitted on the blacklist.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        JPAQuery<BlackListResponseDto> query = jpaQueryFactory
                .selectDistinct(
                        Projections.constructor(
                                BlackListResponseDto.class,
                                QBlackList.blackList.username.as("userName"),
                                QFile.file.fileName.as("fileName"),
                                QFile.file.fileUrl.as("fileUrl")
                        )
                )
                .from(QBlackList.blackList)
                .leftJoin(QFile.file).on(QFile.file.store.eq(QBlackList.blackList.store))
                .where(QBlackList.blackList.username.eq(user.getNickname())
                        .and(QBlackList.blackList.deleted.isFalse()))
                .orderBy(QBlackList.blackList.modifiedDate.desc())
                .offset((page - 1) * 20L)
                .limit(20);

        return query.fetch();
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
