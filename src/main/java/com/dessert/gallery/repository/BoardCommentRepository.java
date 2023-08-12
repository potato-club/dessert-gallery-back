package com.dessert.gallery.repository;

import com.dessert.gallery.entity.BoardComment;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    Slice<BoardComment> findByBoard(Pageable pageable, StoreBoard board);
}
