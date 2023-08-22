package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Bookmark findByStoreBoardAndUser(StoreBoard board, User user);
}
