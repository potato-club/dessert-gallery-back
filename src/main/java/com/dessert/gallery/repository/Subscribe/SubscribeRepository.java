package com.dessert.gallery.repository.Subscribe;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.Subscribe;
import com.dessert.gallery.entity.User;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long>, SubscribeRepositoryCustom {

    boolean existsByStoreAndUserAndDeletedIsFalse(Store store, User user);

    Subscribe findByUser(User user);

    Subscribe findByUserAndStore(User user, Store store);

    boolean existsByStoreAndUser(Store store, User user);

    Long countAllByStoreAndDeletedIsFalse(Store store);

    List<FollowResponseDto> findDistinctFollowResponseDtoByEmail(int page, String email);

    JPAQuery<Long> countByUserEmailAndDeletedIsFalseAndNothingBlackList(String email);
}
