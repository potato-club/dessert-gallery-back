package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BookmarkService {
    String toggleBookmark(StoreBoard board, HttpServletRequest request);
    List<BoardListResponseDto> getBookmarks(HttpServletRequest request);
    boolean isBookmarkBoard(StoreBoard board, User user);
}
