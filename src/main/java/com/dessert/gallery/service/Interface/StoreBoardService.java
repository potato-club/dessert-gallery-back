package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface StoreBoardService {
    BoardResponseDto getBoardById(Long boardId);
    List<BoardListResponseDto> getBoardsByStore(Long storeId);
    void createBoard(Long storeId, BoardRequestDto requestDto,
                     List<MultipartFile> images,HttpServletRequest request);
    void updateBoard(Long boardId, BoardRequestDto updateDto,
                     List<MultipartFile> images, List<FileRequestDto> requestDto, HttpServletRequest request);
    void deleteBoard(Long boardId, HttpServletRequest request);
}
