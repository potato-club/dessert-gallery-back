package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.*;
import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.BadRequestException;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.BoardComment.BoardCommentRepository;
import com.dessert.gallery.repository.StoreBoard.StoreBoardRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.service.Interface.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreBoardServiceImpl implements StoreBoardService {
    private final StoreBoardRepository boardRepository;
    private final StoreRepository storeRepository;
    private final BoardCommentRepository commentRepository;
    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final ImageService imageService;
    @Value("${viewCount.key}")
    private String COOKIE_KEY;

    @Override
    public void createBoard(BoardRequestDto requestDto, List<MultipartFile> images,
                            HttpServletRequest request) throws IOException {
        User user = userService.findUserByToken(request);
        if (user == null) throw new NotFoundException("존재하지 않는 유저", NOT_FOUND_EXCEPTION);

        Store store = storeRepository.findByUser(user);

        if (!validateTag(requestDto.getTags())) {
            throw new BadRequestException("해시태그는 10개까지 입력 가능합니다.", PARAMETER_VALID_EXCEPTION);
        }

        StoreBoard board = new StoreBoard(requestDto, store);
        StoreBoard saveBoard = boardRepository.save(board);

        if (images != null) {
            List<File> files = imageService.uploadImages(images, saveBoard);
            saveBoard.updateImages(files);
        }
    }

    private boolean validateTag(String tags) {
        char target = '#';
        long count = tags.chars().filter(ch -> ch == target).count();

        return count <= 10;
    }

    @Override
    public StoreBoard getBoard(Long boardId) {
        return boardRepository.findByIdAndDeletedIsFalse(boardId);
    }

    @Override
    public Slice<BoardListResponseDto> getBoardsByStore(Long storeId, int page) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("검색된 가게가 없음", NOT_FOUND_EXCEPTION);
        });

        if (page < 1) { // 잘못된 page 값 입력시 1로 초기화
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 15);

        List<StoreBoard> list = boardRepository.findBoardsByStore(store, pageable);

        boolean hasNext = false;

        if (list.size() > pageable.getPageSize()) {
            hasNext = true;
            list.remove(pageable.getPageSize());
        }

        Slice<StoreBoard> sliceList = new SliceImpl<>(list, pageable, hasNext);
        return sliceList.map(BoardListResponseDto::new);
    }

    @Override
    public Slice<BoardListResponseDtoForChat> getBoardListForChat(Long storeId, Long last) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("존재하지 않는 가게", NOT_FOUND_EXCEPTION);
        });

        Pageable pageable = PageRequest.ofSize(10);

        // 해당 가게에서 삭제처리 되지 않은 게시글 리스트를 최신순으로 가져옴
        List<StoreBoard> boardList = boardRepository.findBoardsForChat(store, last, pageable);

        return transType(boardList, pageable);
    }

    private Slice<BoardListResponseDtoForChat> transType(List<StoreBoard> boardList, Pageable pageable) {
        boolean hasNext = false;

        if (boardList.size() > pageable.getPageSize()) {
            hasNext = true;
            boardList.remove(pageable.getPageSize());
        }
        Slice<StoreBoard> slice = new SliceImpl<>(boardList, pageable, hasNext);
        return slice.map(BoardListResponseDtoForChat::new);
    }

    @Override
    public List<BoardListResponseDtoForMap> getBoardsForMap(Store store) {
        List<StoreBoard> boards = boardRepository.findBoardsForMap(store);

        return boards.stream().map(BoardListResponseDtoForMap::new).collect(Collectors.toList());
    }

    @Override
    public BoardResponseDto getBoardDto(Long boardId, HttpServletRequest request, HttpServletResponse response) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        if (board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        int commentCount = commentRepository.countByBoard(board);
        BoardResponseDto dto = new BoardResponseDto(board, commentCount);

        User user = userService.findUserByToken(request);
        if (user != null) {
            boolean bookmarkStatus = bookmarkService.isBookmarkBoard(board, user);
            boolean isOwner = board.getStore().checkOwner(user);
            dto.addUserInfo(bookmarkStatus, isOwner);
        }

        int viewCount = updateViewCount(board, request, response);
        if (viewCount != -1) {
            dto.updateView(viewCount);
        }

        return dto;
    }

    private int updateViewCount(StoreBoard board, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        String value = "[" + board.getId() + "]";
        int viewCount = -1;

        if (cookies != null) {
            for (Cookie old : cookies) {
                if (old.getName().equals(COOKIE_KEY)) {
                    cookie = old;
                    break;
                }
            }
        }

        // 쿠키에 boardId 가 있을 경우만 view 증가 x
        if (cookie != null) {
            if (!cookie.getValue().contains(value)) { // 쿠키에 해당 게시물 저장 x
                cookie.setValue(cookie.getValue() + "_" + value);
                cookie.setMaxAge(getTimeForExpired());
                cookie.setSecure(true);
                cookie.setHttpOnly(true);
                viewCount = board.increaseView();
                response.addHeader("Set-Cookie", convertCookie(cookie));
            }
        } else {
            cookie = new Cookie(COOKIE_KEY, value);
            cookie.setMaxAge(getTimeForExpired());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            viewCount = board.increaseView();
            response.addHeader("Set-Cookie", convertCookie(cookie));
        }

        return viewCount;
    }

    private String convertCookie(Cookie cookie) {
        return ResponseCookie.from(cookie.getName(), cookie.getValue())
                .maxAge(cookie.getMaxAge())
                .secure(cookie.getSecure())
                .httpOnly(cookie.isHttpOnly())
                .path("/")
                .sameSite("None")
                .build().toString();
    }

    // 쿠키 만료 시간 다음날 자정까지로 설정
    private int getTimeForExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        return (int) ChronoUnit.SECONDS.between(now, tomorrow);
    }

    @Override
    public void updateBoard(Long boardId, BoardUpdateDto updateDto,
                            List<MultipartFile> images, HttpServletRequest request) throws IOException {
        StoreBoard board = validateBoard(boardId, request);
        board.updateBoard(updateDto);

        // 삭제할 파일 리스트 저장
        Set<FileDto> set = new HashSet<>(updateDto.getDeleteFiles());

        // 기존 파일 리스트에서 삭제할 파일 리스트 비교
        List<FileRequestDto> originImages = new ArrayList<>();
        List<FileDto> collect = board.getImages().stream().map(FileDto::new).collect(Collectors.toList());

        for (FileDto dto : collect) {
            // 삭제할 파일 리스트에 존재 => 삭제
            if (set.contains(dto)) {
                originImages.add(new FileRequestDto(dto, true));
                board.removeImage(dto);
            }
            // 삭제할 파일 리스트에 존재 X => 삭제 X
            else {
                originImages.add(new FileRequestDto(dto, false));
            }
        }

        // 기존 이미지 없고 새로운 이미지만 있음
        if (CollectionUtils.isEmpty(originImages) && !CollectionUtils.isEmpty(images)) {
            List<File> files = imageService.uploadImages(images, board);
            board.updateImages(files);
        }

        // 기존 이미지 존재 => images 여부 상관없이 update 진행
        if (!CollectionUtils.isEmpty(originImages)) {
            List<File> files = imageService.updateImages(board, images, originImages);
            board.updateImages(files);
        }
    }

    @Override
    public void deleteBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = validateBoard(boardId, request);
        board.deleteBoard();
    }

    private StoreBoard validateBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        User user = userService.findUserByToken(request);
        if (board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        if (board.getStore().getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        return board;
    }
}
