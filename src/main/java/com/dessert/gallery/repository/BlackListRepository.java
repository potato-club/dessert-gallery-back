package com.dessert.gallery.repository;

import com.dessert.gallery.entity.BlackList;
import com.dessert.gallery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    BlackList findByStore(Store store);
    boolean existsByUsernameAndStore(String username, Store store);
}
