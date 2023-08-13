package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreBoardRepository extends JpaRepository<StoreBoard, Long> {
    List<StoreBoard> findByStoreAndDeletedIsFalse(Store store);
    StoreBoard findByIdAndDeletedIsFalse(Long id);
    Long countAllByStore(Store store);
}
