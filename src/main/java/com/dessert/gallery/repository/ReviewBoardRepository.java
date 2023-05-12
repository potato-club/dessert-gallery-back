package com.dessert.gallery.repository;

import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Long> {
    Long countByStore(Store store);
    List<ReviewBoard> findAllByStore(Store store);
    List<ReviewBoard> findTop2ByStoreOrderByCreatedDateDesc(Store store);
}
