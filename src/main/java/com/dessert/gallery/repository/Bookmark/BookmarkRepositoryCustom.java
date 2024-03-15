package com.dessert.gallery.repository.Bookmark;

import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkRepositoryCustom {

    List<Bookmark> findBookmarkByUser(User user, Pageable pageable);
}
