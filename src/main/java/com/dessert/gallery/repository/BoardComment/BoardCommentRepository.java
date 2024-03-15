package com.dessert.gallery.repository.BoardComment;

import com.dessert.gallery.entity.BoardComment;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    Slice<BoardComment> findByBoard(Pageable pageable, StoreBoard board);

    int countByBoard(StoreBoard board);
}
