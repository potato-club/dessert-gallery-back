package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.BoardListResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BookmarkService {
    String toggleBookmark(Long boardId, HttpServletRequest request);
    List<BoardListResponseDto> getBookmarks(HttpServletRequest request);
}
