package com.dessert.gallery.repository.BlackList;

import com.dessert.gallery.entity.BlackList;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, Long>, BlackListRepositoryCustom {

    BlackList findByStoreAndUser(Store store, User user);

    boolean existsByUserAndStore(User user, Store store);

    boolean existsByUserAndStoreAndDeletedIsFalse(User user, Store store);

    boolean existsByUserAndStoreAndDeletedIsTrue(User user, Store store);
}
