package com.dessert.gallery.repository.BlackList;

import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
import com.dessert.gallery.entity.BlackList;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlackListRepository extends JpaRepository<BlackList, Long>, BlackListRepositoryCustom {

    BlackList findByStoreAndUser(Store store, User user);

    boolean existsByUserAndStoreAndDeletedIsFalse(User user, Store store);

    boolean existsByUserAndStoreAndDeletedIsTrue(User user, Store store);

    List<BlackListResponseDto> getBlackListInStore(int page, Store store);

    JPAQuery<Long> countByStoreAndDeletedIsFalse(Store store);
}
