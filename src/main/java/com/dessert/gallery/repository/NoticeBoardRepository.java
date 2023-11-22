package com.dessert.gallery.repository;

import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.enums.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
    List<NoticeBoard> findByStoreAndDeletedIsFalse(Store store);
    List<NoticeBoard> findByStoreAndDeletedIsFalseAndType(Store store, NoticeType type);
    List<NoticeBoard> findByStoreAndDeletedFalseAndExposedTrue(Store store);
    NoticeBoard findByIdAndDeletedIsFalse(Long id);
}
