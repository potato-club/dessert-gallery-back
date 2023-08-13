package com.dessert.gallery.repository;

import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Long> {
    Long countByStore(Store store);
    List<ReviewBoard> findAllByStore(Store store);
}
