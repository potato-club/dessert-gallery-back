package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreBoardRepository extends JpaRepository<StoreBoard, Long> {
    List<StoreBoard> findByStoreAndDeletedIsFalse(Store store);
    StoreBoard findByIdAndDeletedIsFalse(Long id);
}
