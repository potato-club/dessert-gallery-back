package com.dessert.gallery.repository;

import com.dessert.gallery.entity.BlackList;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    BlackList findByStore(Store store);
    boolean existsByUserAndStore(User user, Store store);
}
