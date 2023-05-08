package com.dessert.gallery.repository;

import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Long> {
    Long countByStore(Store store);
}
