package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.entity.Bookmark;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.repository.Bookmark.BookmarkRepository;
import com.dessert.gallery.service.Interface.BookmarkService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;

    @Override
    public String toggleBookmark(StoreBoard board, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        Bookmark findBookmark = bookmarkRepository.findByStoreBoardAndUser(board, user);

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

    @Override
    public Slice<BoardListResponseDto> getBookmarks(HttpServletRequest request, int page) {
        User user = userService.findUserByToken(request);

        if (page < 1) { // 잘못된 page 값 입력시 1로 초기화
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 16);

        List<Bookmark> bookmarkList = bookmarkRepository.findBookmarkByUser(user, pageable);

        boolean hasNext = false;

        if (bookmarkList.size() > pageable.getPageSize()) {
            hasNext = true;
            bookmarkList.remove(pageable.getPageSize());
        }

        Slice<Bookmark> slice = new SliceImpl<>(bookmarkList, pageable, hasNext);
        return slice.map(bookmark -> new BoardListResponseDto(bookmark.getStoreBoard()));
    }

    @Override
    public boolean isBookmarkBoard(StoreBoard board, User user) {
        return bookmarkRepository.existsByStoreBoardAndUser(board, user);
    }
}
