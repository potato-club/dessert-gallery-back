package com.dessert.gallery.repository.Subscribe;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.Subscribe;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long>, SubscribeRepositoryCustom {

    boolean existsByStoreAndUserAndDeletedIsFalse(Store store, User user);

    Subscribe findByUser(User user);

    Subscribe findByUserAndStore(User user, Store store);

    boolean existsByStoreAndUser(Store store, User user);

    Long countAllByStoreAndDeletedIsFalse(Store store);
}
