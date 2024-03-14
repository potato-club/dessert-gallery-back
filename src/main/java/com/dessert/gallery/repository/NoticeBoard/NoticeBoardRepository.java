package com.dessert.gallery.repository.NoticeBoard;

import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long>, NoticeBoardRepositoryCustom {

    List<NoticeBoard> findByStoreAndDeletedFalseAndExposedTrue(Store store);
    NoticeBoard findByIdAndDeletedIsFalse(Long id);
}
