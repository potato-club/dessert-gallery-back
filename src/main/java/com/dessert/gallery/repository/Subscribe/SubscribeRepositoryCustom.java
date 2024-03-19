package com.dessert.gallery.repository.Subscribe;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import com.dessert.gallery.entity.Store;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.List;

public interface SubscribeRepositoryCustom {

    List<FollowResponseDto> findDistinctFollowResponseDtoByEmail(int page, String email);

    JPAQuery<Long> countByUserEmailAndDeletedIsFalseAndNothingBlackList(String email);

    boolean existsFollowingManager(Store store);
}
