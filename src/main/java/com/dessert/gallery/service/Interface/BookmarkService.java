package com.dessert.gallery.service.Interface;

import javax.servlet.http.HttpServletRequest;

public interface BookmarkService {
    String toggleBookmark(Long boardId, HttpServletRequest request);
}
