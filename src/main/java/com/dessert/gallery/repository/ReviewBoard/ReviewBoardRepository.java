package com.dessert.gallery.repository.ReviewBoard;

import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Long>, ReviewBoardRepositoryCustom {
    Long countByStore(Store store);
    Page<ReviewBoard> findAllByStore(Pageable pageable, Store store);
    Slice<ReviewBoard> findAllByUserAndCreatedDateGreaterThanEqual(Pageable pageable, User user, LocalDateTime date);
    Slice<ReviewBoard> findAllByUser(Pageable pageable, User user);
}
