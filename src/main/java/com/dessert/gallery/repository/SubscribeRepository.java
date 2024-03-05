package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.Subscribe;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    boolean existsByStoreAndUserAndDeletedIsFalse(Store store, User user);

    Subscribe findByUser(User user);

    Subscribe findByUserAndStore(User user, Store store);

    boolean existsByStoreAndUser(Store store, User user);

    Long countAllByStoreAndDeletedIsFalse(Store store);
}
