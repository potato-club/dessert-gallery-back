package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Bookmark findByBoardAndUser(StoreBoard board, User user);
}
