package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.NoticeBoardRepository;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import com.dessert.gallery.service.Interface.StoreService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NoticeBoardServiceImpl implements NoticeBoardService {
    private final NoticeBoardRepository noticeRepository;
    private final StoreService storeService;
    private final UserService userService;

    // 공지사항 단건 조회
    @Override
    public NoticeResponseDto getNoticeById(Long noticeId) {
        NoticeBoard noticeBoard = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        if (noticeBoard == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        return new NoticeResponseDto(noticeBoard);
    }

    // 메인 노출되는 공지사항 리스트 출력
    @Override
    public List<NoticeListDto> getNoticesByStore(Long storeId) {
        Store store = storeService.getStore(storeId);

        List<NoticeBoard> notices = noticeRepository.findByStoreAndDeletedFalseAndExposedTrue(store);
        if (notices == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);

        return notices.stream().map(NoticeListDto::new).collect(Collectors.toList());
    }

    // 사장님 마이페이지 공지사항 리스트 출력
    @Override
    public List<NoticeListDto> getNoticesByOwner(HttpServletRequest request) {
        User owner = userService.findUserByToken(request);
        if (owner == null) throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        Store store = storeService.getStoreByUser(owner);
        if (store == null) throw new NotFoundException("존재하지 않는 가게", NOT_FOUND_EXCEPTION);

        List<NoticeBoard> notices = noticeRepository.findByStoreAndDeletedIsFalse(store);
        if (notices == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);

        return notices.stream().map(NoticeListDto::new).collect(Collectors.toList());
    }

    @Override
    public void createNotice(Long storeId, NoticeRequestDto requestDto,
                             HttpServletRequest request) {
        Store store = storeService.getStore(storeId);
        User user = userService.findUserByToken(request);

        if (store.getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        NoticeBoard notice = new NoticeBoard(requestDto, store);
        noticeRepository.save(notice);
    }

    @Override
    public void updateNotice(Long noticeId, NoticeRequestDto updateDto,
                             HttpServletRequest request) {
        NoticeBoard notice = validateNotice(noticeId, request);
        notice.updateNotice(updateDto);
    }

    @Override
    public void deleteNotice(Long noticeId, HttpServletRequest request) {
        NoticeBoard notice = validateNotice(noticeId, request);
        notice.deleteNotice();
    }

    private NoticeBoard validateNotice(Long noticeId, HttpServletRequest request) {
        NoticeBoard notice = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        User user = userService.findUserByToken(request);

        if (notice == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        if (notice.getStore().getUser() != user)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        return notice;
    }
}
