package com.dessert.gallery.controller;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.service.Interface.StoreBoardService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Api(tags = {"Store Board Controller"})
public class StoreBoardController {
    private final StoreBoardService boardService;

    @Operation(summary = "가게의 모든 게시글 조회")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<BoardListResponseDto>> getBoardListByStore(@PathVariable(name = "storeId") Long storeId) {
        List<BoardListResponseDto> boards = boardService.getBoardsByStore(storeId);
        return ResponseEntity.ok(boards);
    }

    @Operation(summary = "게시글 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable(name = "boardId") Long boardId) {
        BoardResponseDto dto = boardService.getBoardById(boardId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "가게 게시글 작성")
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<String> createStoreBoard(@PathVariable(name = "storeId") Long storeId,
                                                   @RequestPart BoardRequestDto boardDto,
                                                   @RequestPart List<MultipartFile> images,
                                                   HttpServletRequest request) {
        boardService.createBoard(storeId, boardDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 생성 완료");
    }

    @Operation(summary = "가게 게시글 수정")
    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateStoreBoard(@PathVariable(name = "boardId") Long boardId,
                                                   @RequestBody BoardRequestDto updateDto,
                                                   @RequestPart(required = false) List<MultipartFile> images,
                                                   HttpServletRequest request) {
        boardService.updateBoard(boardId, updateDto, images, request);
        return ResponseEntity.ok("게시글 수정 완료");
    }

    @Operation(summary = "가게 게시글 삭제 상태로 변경")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteStoreBoard(@PathVariable(name = "boardId") Long boardId,
                                                   HttpServletRequest request) {
        boardService.deleteBoard(boardId, request);
        return ResponseEntity.ok("게시글 삭제 완료");
    }
}
