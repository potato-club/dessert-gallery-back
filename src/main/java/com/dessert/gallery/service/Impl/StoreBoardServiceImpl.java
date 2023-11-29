package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.service.Interface.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreBoardServiceImpl implements StoreBoardService {
    private final StoreBoardRepository boardRepository;
    private final StoreService storeService;
    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final ImageService imageService;

    @Override
    public void createBoard(BoardRequestDto requestDto, List<MultipartFile> images,
                            HttpServletRequest request) throws IOException {
        User user = userService.findUserByToken(request);
        if (user == null) throw new NotFoundException("존재하지 않는 유저", NOT_FOUND_EXCEPTION);

        Store store = storeService.getStoreByUser(user);
        StoreBoard board = new StoreBoard(requestDto, store);
        StoreBoard saveBoard = boardRepository.save(board);

        if (images != null) {
            List<File> files = imageService.uploadImages(images, saveBoard);
            saveBoard.updateImages(files);
        }
    }

    @Override
    public StoreBoard getBoard(Long boardId) {
        return boardRepository.findByIdAndDeletedIsFalse(boardId);
    }

    @Override
    public List<BoardListResponseDto> getBoardsByStore(Long storeId) {
        Store store = storeService.getStore(storeId);
        List<StoreBoard> boards = boardRepository.findByStoreAndDeletedIsFalse(store);
        if (boards == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        return boards.stream().map(BoardListResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public BoardResponseDto getBoardDto(Long boardId, HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        if (board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        BoardResponseDto dto = new BoardResponseDto(board);

        User user = userService.findUserByToken(request);
        if (user != null) {
            boolean bookmarkStatus = bookmarkService.isBookmarkBoard(board, user);
            boolean isOwner = board.getStore().checkOwner(user);
            dto.addUserInfo(bookmarkStatus, isOwner);
        }
        return dto;
    }

    @Override
    public void updateBoard(Long boardId, BoardRequestDto updateDto, List<MultipartFile> images,
                            List<FileRequestDto> requestDto, HttpServletRequest request) throws IOException {
        StoreBoard board = validateBoard(boardId, request);
        board.updateBoard(updateDto);

        // images 가 null 이면 빈 배열 생성
        if (images == null) images = new ArrayList<>();

        List<File> files;
        // requestDto 가 null 이고 images 가 있다면 이미지 업로드와 같음
        if (requestDto == null && images.size() != 0) {
            files = imageService.uploadImages(images, board);
        } else {
            files = imageService.updateImages(board, images, requestDto);
        }
        board.imageClear();
        board.updateImages(files);
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
