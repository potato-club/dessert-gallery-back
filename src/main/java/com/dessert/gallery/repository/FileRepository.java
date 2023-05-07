package com.dessert.gallery.repository;

import com.dessert.gallery.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByNoticeBoardId(Long id);

    List<File> findByReviewBoardId(Long id);

    List<File> findByStoreBoardId(Long id);

    List<File> findByStoreId(Long id);

    List<File> findByUserId(Long id);
}
