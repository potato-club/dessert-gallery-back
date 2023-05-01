package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.entity.StoreBoard;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StoreBoardService {
    void createBoard(Long storeId, BoardRequestDto requestDto, HttpServletRequest request);
    List<BoardListResponseDto> getBoardsByStore(Long storeId);
    BoardResponseDto getBoardById(Long boardId);
    void updateBoard(Long boardId, BoardRequestDto requestDto, HttpServletRequest request);
    void deleteBoard(Long boardId, HttpServletRequest request);
}
