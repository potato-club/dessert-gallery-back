package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.comment.CommentRequestDto;
import com.dessert.gallery.dto.comment.CommentResponseDto;
import org.springframework.data.domain.Slice;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    Slice<CommentResponseDto> getComments(Long boardId, int page, HttpServletRequest request);
    CommentResponseDto addComment(Long boardId, CommentRequestDto requestDto, HttpServletRequest request);
    String removeComment(Long commentId, HttpServletRequest request);
}
