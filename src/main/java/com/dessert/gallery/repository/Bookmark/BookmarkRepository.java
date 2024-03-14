package com.dessert.gallery.repository.Bookmark;

import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    List<Bookmark> findByUser(User user);
    Bookmark findByStoreBoardAndUser(StoreBoard board, User user);
    boolean existsByStoreBoardAndUser(StoreBoard board, User user);
}
