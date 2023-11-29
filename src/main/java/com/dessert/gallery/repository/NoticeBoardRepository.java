package com.dessert.gallery.repository;

import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
    List<NoticeBoard> findByStoreAndDeletedFalseAndExposedTrue(Store store);
    NoticeBoard findByIdAndDeletedIsFalse(Long id);
}
