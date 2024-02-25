package com.dessert.gallery.controller;

import com.dessert.gallery.dto.board.BoardListResponseDto;
import com.dessert.gallery.dto.board.BoardRequestDto;
import com.dessert.gallery.dto.board.BoardResponseDto;
import com.dessert.gallery.dto.board.BoardUpdateDto;
import com.dessert.gallery.service.Interface.BookmarkService;
import com.dessert.gallery.service.Interface.StoreBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Tag(name = "Store Board Controller", description = "가게 게시판 API")
public class StoreBoardController {
    private final StoreBoardService boardService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "게시글 조회")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable(name = "boardId") Long boardId,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        BoardResponseDto dto = boardService.getBoardDto(boardId, request, response);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "가게의 모든 게시글 조회")
    @GetMapping("/stores/{storeId}")
    public Slice<BoardListResponseDto> getBoardListByStore(@PathVariable(name = "storeId") Long storeId,
                                                           @Parameter(name = "page", description = "페이지 정보")
                                                           @RequestParam(value = "page",
                                                                   required = false,
                                                                   defaultValue = "1") int page) {
        return boardService.getBoardsByStore(storeId, page);
    }

    @Operation(summary = "가게 게시글 작성")
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> createStoreBoard(@Parameter(description = "게시글 정보 - BoardRequestDto", content =
                                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                        @Validated
                                                            @RequestPart(required = false) BoardRequestDto boardDto,
                                                   @Parameter(description = "업로드 이미지 리스트", content =
                                                    @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                        @RequestPart(required = false) List<MultipartFile> images,
                                                   HttpServletRequest request) throws IOException {
        boardService.createBoard(boardDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 생성 완료");
    }

    @Operation(summary = "게시글 북마크 토글")
    @PostMapping("/{boardId}/bookmark")
    public ResponseEntity<String> bookmarkBoard(@PathVariable(name = "boardId") Long boardId,
                                                HttpServletRequest request) {
        String res = bookmarkService.toggleBookmark(boardService.getBoard(boardId), request);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "가게 게시글 수정")
    @PutMapping(value = "/{boardId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateStoreBoard(@PathVariable(name = "boardId") Long boardId,
                                                   @Parameter(description = "수정할 게시글 정보 - BoardUpdateDto", content =
                                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                        @Validated
                                                            @RequestPart(name = "updateDto") BoardUpdateDto updateDto,
                                                   @Parameter(description = "추가할 이미지 리스트", content =
                                                    @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                        @RequestPart(required = false) List<MultipartFile> images,
                                                   HttpServletRequest request) throws IOException {
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
