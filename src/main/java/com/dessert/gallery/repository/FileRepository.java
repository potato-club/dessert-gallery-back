package com.dessert.gallery.repository;

import com.dessert.gallery.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByNoticeBoard(NoticeBoard notice);

    List<File> findByReviewBoard(ReviewBoard review);

    List<File> findByStoreBoard(StoreBoard board);

    List<File> findByStore(Store store);

    List<File> findByUser(User user);
}