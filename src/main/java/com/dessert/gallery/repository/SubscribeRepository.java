package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.Subscribe;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    boolean existsByStoreAndUserAndDeletedIsFalse(Store store, User user);
}
