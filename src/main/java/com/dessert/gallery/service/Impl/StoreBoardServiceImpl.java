package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.StoreBoardService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.S3.S3Service;
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

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreBoardServiceImpl implements StoreBoardService {
    private final StoreBoardRepository boardRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @Override
    public void createBoard(Long storeId, BoardRequestDto requestDto, List<MultipartFile> images,
                            HttpServletRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        User user = userService.findUserByToken(request);
        if(store.getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        StoreBoard board = new StoreBoard(requestDto, store);
        List<File> files = saveImage(images, board);
        board.setImages(files);
        boardRepository.save(board);
    }

    @Override
    public List<BoardListResponseDto> getBoardsByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("store is not exist"));
        List<StoreBoard> boards = boardRepository.findByStoreAndDeletedIsFalse(store);
        if(boards == null) throw new RuntimeException("게시물 없음");
        return boards.stream().map(BoardListResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public BoardResponseDto getBoardById(Long boardId) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        if(board == null) throw new RuntimeException("게시물 없음");
        BoardResponseDto dto = new BoardResponseDto(board);
        return dto;
    }

    @Override
    public void updateBoard(Long boardId, BoardRequestDto requestDto, List<MultipartFile> images,
                            HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        User user = userService.findUserByToken(request);
        if(board == null) throw new RuntimeException("게시물 없음");
        if(board.getStore().getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        if(!images.isEmpty()) {
            List<File> files = updateImage(board, images);
            board.setImages(files);
        }
        board.updateBoard(requestDto);
    }

    @Override
    public void deleteBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        User user = userService.findUserByToken(request);
        if(board == null) throw new RuntimeException("게시물 없음");
        if(board.getStore().getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        board.deleteBoard();
    }

    private List<File> saveImage(List<MultipartFile> images, StoreBoard board) {
        try {
            return s3Service.uploadImages(images, board);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 에러");
        }
    }

    private List<File> updateImage(StoreBoard board, List<MultipartFile> images) {
        try {
            return s3Service.updateFiles(board, images);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업데이트 에러");
        }
    }
}
