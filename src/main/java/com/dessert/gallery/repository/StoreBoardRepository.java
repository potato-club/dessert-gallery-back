package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBoardRepository extends JpaRepository<StoreBoard, Long> {
    StoreBoard findByIdAndDeletedIsFalse(Long id);
    Long countAllByStoreAndDeletedIsFalse(Store store);
}
