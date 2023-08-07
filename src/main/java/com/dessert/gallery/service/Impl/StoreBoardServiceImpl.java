package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.S3Exception;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.service.Interface.StoreBoardService;
import com.dessert.gallery.service.Interface.StoreService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
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
    private final S3Service s3Service;

    @Override
    public void createBoard(Long storeId, BoardRequestDto requestDto, List<MultipartFile> images,
                            HttpServletRequest request) {
        Store store = storeService.getStore(storeId);
        User user = userService.findUserByToken(request);
        if(store.getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        StoreBoard board = new StoreBoard(requestDto, store);
        StoreBoard saveBoard = boardRepository.save(board);
        List<File> files = saveImage(images, saveBoard);
        saveBoard.setImages(files);
    }

    @Override
    public StoreBoard getBoard(Long boardId) {
        return boardRepository.findByIdAndDeletedIsFalse(boardId);
    }

    @Override
    public List<BoardListResponseDto> getBoardsByStore(Long storeId) {
        Store store = storeService.getStore(storeId);
        List<StoreBoard> boards = boardRepository.findByStoreAndDeletedIsFalse(store);
        if(boards == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        return boards.stream().map(BoardListResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public Integer getPostCount(Store store) {
        return Math.toIntExact(boardRepository.countAllByStore(store));
    }

    @Override
    public BoardResponseDto getBoardDto(Long boardId) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        if(board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        BoardResponseDto dto = new BoardResponseDto(board);
        return dto;
    }

    @Override
    public void updateBoard(Long boardId, BoardRequestDto updateDto, List<MultipartFile> images,
                            List<FileRequestDto> requestDto, HttpServletRequest request) {
        StoreBoard board = validateBoard(boardId, request);
        if(!images.isEmpty()) {
            List<File> files = updateImage(board, images, requestDto);
            board.setImages(files);
        }
        board.updateBoard(updateDto);
    }

    @Override
    public void deleteBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = validateBoard(boardId, request);
        board.deleteBoard();
    }

    public StoreBoard validateBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        User user = userService.findUserByToken(request);
        if(board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        if(board.getStore().getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        return board;
    }

    private List<File> saveImage(List<MultipartFile> images, StoreBoard board) {
        try {
            return s3Service.uploadImages(images, board);
        } catch (IOException e) {
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }

    private List<File> updateImage(StoreBoard board, List<MultipartFile> images, List<FileRequestDto> requestDto) {
        try {
            return s3Service.updateFiles(board, images, requestDto);
        } catch (IOException e) {
            throw new S3Exception("이미지 업데이트 에러", RUNTIME_EXCEPTION);
        }
    }
}
