package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.comment.CommentRequestDto;
import com.dessert.gallery.dto.comment.CommentResponseDto;
import com.dessert.gallery.entity.BoardComment;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.StoreBoard;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.BoardComment.BoardCommentRepository;
import com.dessert.gallery.repository.File.FileRepository;
import com.dessert.gallery.service.Interface.BlackListService;
import com.dessert.gallery.service.Interface.CommentService;
import com.dessert.gallery.service.Interface.StoreBoardService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.NOT_ALLOW_WRITE_EXCEPTION;
import static com.dessert.gallery.error.ErrorCode.NOT_FOUND_EXCEPTION;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final BoardCommentRepository commentRepository;
    private final StoreBoardService boardService;
    private final BlackListService blackListService;
    private final FileRepository fileRepository;
    private final UserService userService;

    @Override
    public Slice<CommentResponseDto> getComments(Long boardId, int page, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        StoreBoard board = boardService.getBoard(boardId);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));

        Slice<BoardComment> comments = commentRepository.findByBoard(pageRequest, board);

        List<User> userList = comments.getContent()
                .stream().map(BoardComment::getUser)
                .collect(Collectors.toList());

        List<File> userImageList = fileRepository.findByUserIn(userList);

        if (user == null) {
            return comments.map(comment -> {
                Optional<File> image = userImageList.stream()
                        .filter(file -> file.getUser().equals(comment.getUser()))
                        .findFirst();


                return new CommentResponseDto(comment, false, image.orElse(null));
            });
        }

        return comments.map(comment -> {
            Optional<File> image = userImageList.stream()
                    .filter(file -> file.getUser().equals(comment.getUser()))
                    .findFirst();

            boolean isWriter = Objects.equals(user.getNickname(), comment.getUser().getNickname());

            return new CommentResponseDto(comment, isWriter, image.orElse(null));
        });
    }

    @Override
    public CommentResponseDto addComment(Long boardId, CommentRequestDto requestDto, HttpServletRequest request) {
        User user = userService.findUserByToken(request);
        StoreBoard board = boardService.getBoard(boardId);
        blackListService.validateBlackList(board.getStore(), user);

        BoardComment comment = BoardComment.builder()
                .comment(requestDto.getComment())
                .board(board)
                .user(user)
                .build();

        BoardComment saveComment = commentRepository.save(comment);
        List<File> userImage = fileRepository.findByUser(user);
        return new CommentResponseDto(saveComment, true,
                userImage.size() == 0 ? null : userImage.get(0));
    }

    @Override
    public String removeComment(Long commentId, HttpServletRequest request) {
        BoardComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글", NOT_FOUND_EXCEPTION));
        User findUser = userService.findUserByToken(request);
        if(findUser != comment.getUser()) {
            throw new UnAuthorizedException("작성자만 삭제 가능합니다", NOT_ALLOW_WRITE_EXCEPTION);
        }
        commentRepository.delete(comment);
        return "삭제 완료";
    }
}
