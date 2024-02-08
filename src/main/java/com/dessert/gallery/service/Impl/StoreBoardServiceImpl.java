package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.*;
import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StoreBoardServiceImpl implements StoreBoardService {
    private final JPAQueryFactory jpaQueryFactory;
    private final StoreBoardRepository boardRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final ImageService imageService;

    @Override
    public void createBoard(BoardRequestDto requestDto, List<MultipartFile> images,
                            HttpServletRequest request) throws IOException {
        User user = userService.findUserByToken(request);
        if (user == null) throw new NotFoundException("존재하지 않는 유저", NOT_FOUND_EXCEPTION);

        Store store = storeRepository.findByUser(user);
        StoreBoard board = new StoreBoard(requestDto, store);
        StoreBoard saveBoard = boardRepository.save(board);

        if (images != null) {
            List<File> files = imageService.uploadImages(images, saveBoard);
            saveBoard.updateImages(files);
        }
    }

    @Override
    public StoreBoard getBoard(Long boardId) {
        return boardRepository.findByIdAndDeletedIsFalse(boardId);
    }

    @Override
    public Slice<BoardListResponseDto> getBoardsByStore(Long storeId, int page) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("검색된 가게가 없음", NOT_FOUND_EXCEPTION);
        });

        if (page < 1) { // 잘못된 page 값 입력시 1로 초기화
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 15);

        BooleanBuilder whereQuery = new BooleanBuilder();
        whereQuery.and(QStoreBoard.storeBoard.deleted.isFalse());

        List<StoreBoard> list = jpaQueryFactory
                .select(QStoreBoard.storeBoard).from(QStoreBoard.storeBoard)
                .where(whereQuery.and(QStoreBoard.storeBoard.store.eq(store)))
                .orderBy(QStoreBoard.storeBoard.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1).fetch();

        boolean hasNext = false;

        if (list.size() > pageable.getPageSize()) {
            hasNext = true;
            list.remove(pageable.getPageSize());
        }

        Slice<StoreBoard> sliceList = new SliceImpl<>(list, pageable, hasNext);
        return sliceList.map(BoardListResponseDto::new);
    }

    @Override
    public List<BoardListResponseDtoForMap> getBoardsForMap(Store store) {
        List<StoreBoard> boards = jpaQueryFactory.select(QStoreBoard.storeBoard).from(QStoreBoard.storeBoard)
                .where(QStoreBoard.storeBoard.store.eq(store).and(QStoreBoard.storeBoard.deleted.isFalse()))
                .orderBy(QStoreBoard.storeBoard.createdDate.desc())
                .limit(4).fetch();

        return boards.stream().map(BoardListResponseDtoForMap::new).collect(Collectors.toList());
    }

    @Override
    public BoardResponseDto getBoardDto(Long boardId, HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        if (board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        BoardResponseDto dto = new BoardResponseDto(board);

        User user = userService.findUserByToken(request);
        if (user != null) {
            boolean bookmarkStatus = bookmarkService.isBookmarkBoard(board, user);
            boolean isOwner = board.getStore().checkOwner(user);
            dto.addUserInfo(bookmarkStatus, isOwner);
        }
        return dto;
    }

    @Override
    public void updateBoard(Long boardId, BoardUpdateDto updateDto,
                            List<MultipartFile> images, HttpServletRequest request) throws IOException {
        StoreBoard board = validateBoard(boardId, request);
        board.updateBoard(updateDto);

        // 남길 파일 리스트 저장
        Set<FileDto> set = new HashSet<>(updateDto.getNonDeleteFiles());

        // 기존 파일 리스트에서 남길 파일 리스트 비교
        List<FileRequestDto> originImages = new ArrayList<>();
        List<FileDto> collect = board.getImages().stream().map(FileDto::new).collect(Collectors.toList());

        for (FileDto dto : collect) {
            // 남길 파일에 존재 => 삭제 X
            if (set.contains(dto)) {
                originImages.add(new FileRequestDto(dto, false));
            }

            // 남길 파일에 존재 X => board 에서도 삭제
            else {
                FileRequestDto deleteDto = new FileRequestDto(dto, true);
                originImages.add(deleteDto);
                board.removeImage(dto);
            }
        }

        // 기존 이미지 없고 새로운 이미지만 있음
        if (CollectionUtils.isEmpty(originImages) && !CollectionUtils.isEmpty(images)) {
            List<File> files = imageService.uploadImages(images, board);
            board.updateImages(files);
        }

        // 기존 이미지 존재 => images 여부 상관없이 update 진행
        if (!CollectionUtils.isEmpty(originImages)) {
            List<File> files = imageService.updateImages(board, images, originImages);
            board.updateImages(files);
        }
    }

    @Override
    public void deleteBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = validateBoard(boardId, request);
        board.deleteBoard();
    }

    private StoreBoard validateBoard(Long boardId, HttpServletRequest request) {
        StoreBoard board = boardRepository.findByIdAndDeletedIsFalse(boardId);
        User user = userService.findUserByToken(request);
        if (board == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        if (board.getStore().getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        return board;
    }
}
