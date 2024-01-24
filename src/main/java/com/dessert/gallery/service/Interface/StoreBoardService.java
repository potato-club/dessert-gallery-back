package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardListResponseDtoForMap;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface StoreBoardService {
    StoreBoard getBoard(Long boardId);
    BoardResponseDto getBoardDto(Long boardId, HttpServletRequest request);
    Slice<BoardListResponseDto> getBoardsByStore(Long storeId, Long last);
    List<BoardListResponseDtoForMap> getBoardsForMap(Store store);
    void createBoard(BoardRequestDto requestDto,
                     List<MultipartFile> images,HttpServletRequest request) throws IOException;
    void updateBoard(Long boardId, BoardRequestDto updateDto,
                     List<MultipartFile> images, HttpServletRequest request) throws IOException;
    void deleteBoard(Long boardId, HttpServletRequest request);
}
