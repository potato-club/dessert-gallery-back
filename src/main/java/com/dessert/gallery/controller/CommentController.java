package com.dessert.gallery.controller;

import com.dessert.gallery.dto.comment.CommentRequestDto;
import com.dessert.gallery.dto.comment.CommentResponseDto;
import com.dessert.gallery.service.Interface.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Board Comment Controller", description = "게시판 댓글 API")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "게시글 댓글 조회")
    @GetMapping("/{boardId}")
    public Slice<CommentResponseDto> getComments(@PathVariable(name = "boardId") Long boardId,
                                                 @RequestParam(required = false,
                                                         defaultValue = "1", value = "p") int page) {
        return commentService.getComments(boardId, page);
    }

    @Operation(summary = "게시글 댓글 작성")
    @PostMapping("/{boardId}")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable(name = "boardId") Long boardId,
                                             @RequestBody CommentRequestDto requestDto,
                                             HttpServletRequest request) {
        CommentResponseDto res = commentService.addComment(boardId, requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("")
    public ResponseEntity<String> removeComment(@RequestParam(name = "id") Long id,
                                                HttpServletRequest request) {
        String res = commentService.removeComment(id, request);
        return ResponseEntity.ok(res);
    }
}
