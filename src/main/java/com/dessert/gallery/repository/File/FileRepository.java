package com.dessert.gallery.repository.File;

import com.dessert.gallery.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {
    List<File> findByNoticeBoard(NoticeBoard notice);

    List<File> findByReviewBoard(ReviewBoard review);

    List<File> findByStoreBoard(StoreBoard board);

    List<File> findByStore(Store store);

    List<File> findByUser(User user);

    List<File> findByUserIn(List<User> userList);
}
