package com.dessert.gallery.service.Impl;

import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.repository.BookmarkRepository;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.service.Interface.BookmarkService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final StoreBoardRepository boardRepository;

    @Override
    public String toggleBookmark(Long boardId, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        Bookmark findBookmark = bookmarkRepository.findByBoardAndUser(board, user);

        if(findBookmark == null) {
            Bookmark bookmark = new Bookmark(user, board);
            Bookmark saveBookmark = bookmarkRepository.save(bookmark);
            user.addBookmark(saveBookmark);
            return "북마크 생성";
        } else {
            user.removeBookmark(findBookmark);
            bookmarkRepository.delete(findBookmark);
            return "북마크 삭제";
        }
    }
}
