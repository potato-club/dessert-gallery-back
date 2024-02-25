package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.board.*;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface StoreBoardService {
    StoreBoard getBoard(Long boardId);
    BoardResponseDto getBoardDto(Long boardId, HttpServletRequest request, HttpServletResponse response);
    Slice<BoardListResponseDto> getBoardsByStore(Long storeId, int page);
    Slice<BoardListResponseDtoForChat> getBoardListForChat(Long storeId, Long last);
    List<BoardListResponseDtoForMap> getBoardsForMap(Store store);
    void createBoard(BoardRequestDto requestDto,
                     List<MultipartFile> images,HttpServletRequest request) throws IOException;
    void updateBoard(Long boardId, BoardUpdateDto updateDto,
                     List<MultipartFile> images, HttpServletRequest request) throws IOException;
    void deleteBoard(Long boardId, HttpServletRequest request);
}
