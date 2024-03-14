package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.follow.FollowResponseDto;
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
import com.dessert.gallery.service.Interface.FollowService;
import com.querydsl.jpa.impl.JPAQuery;
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
public class FollowServiceImpl implements FollowService {

    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BlackListRepository blackListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void addStoreFollowing(Long storeId, HttpServletRequest request) {
        String email = this.getUserEmail(request);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getUserRole().equals(UserRole.MANAGER)) {
            throw new UnAuthorizedException("Do not subscribe store.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new UnAuthorizedException("Not Found data", ErrorCode.ACCESS_DENIED_EXCEPTION);
        });

        if (blackListRepository.existsByUserAndStoreAndDeletedIsTrue(user, store)) {
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
    @Transactional
    public void removeFollowing(Long storeId, HttpServletRequest request) {
        String email = this.getUserEmail(request);
        UserRole userRole = jwtTokenProvider.getRoles(email);

        if (userRole.equals(UserRole.MANAGER)) {
            throw new UnAuthorizedException("Access isn't permitted on the unfollowing.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        Store store = storeRepository.findById(storeId).orElseThrow();
        Subscribe subUser = subscribeRepository.findByUserAndStore(user, store);

        if (subUser == null) {
            throw new NotFoundException("No Subscribe Data", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        subUser.setDeleted(true);
    }

    @Override
    public Page<FollowResponseDto> getFollowingList(int page, HttpServletRequest request) {

        String email = this.getUserEmail(request);
        UserRole userRole = jwtTokenProvider.getRoles(email);

        if (userRole.equals(UserRole.MANAGER)) {
            throw new UnAuthorizedException("Access isn't permitted on the following.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (page < 1) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 20);

        List<FollowResponseDto> list = subscribeRepository.findDistinctFollowResponseDtoByEmail(page, email);
        JPAQuery<Long> countQuery = subscribeRepository.countByUserEmailAndDeletedIsFalseAndNothingBlackList(email);

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);
    }

    private String getUserEmail(HttpServletRequest request) {
        return jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
    }
}
