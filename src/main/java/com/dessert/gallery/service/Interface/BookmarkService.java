package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import org.springframework.data.domain.Slice;

import javax.servlet.http.HttpServletRequest;

public interface BookmarkService {
    String toggleBookmark(StoreBoard board, HttpServletRequest request);
    Slice<BoardListResponseDto> getBookmarks(HttpServletRequest request, int page);
    boolean isBookmarkBoard(StoreBoard board, User user);
}
