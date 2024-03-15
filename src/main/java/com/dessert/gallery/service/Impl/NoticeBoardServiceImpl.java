package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.NoticeType;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.NoticeBoard.NoticeBoardRepository;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final StoreRepository storeRepository;
    private final UserService userService;

    // 메인 노출되는 공지사항 리스트 출력
    @Override
    public List<NoticeListDto> getNoticesByStore(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("검색된 가게가 없음", NOT_FOUND_EXCEPTION);
        });

        List<NoticeBoard> notices = noticeRepository.findByStoreAndDeletedFalseAndExposedTrue(store);
        if (notices == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);

        return notices.stream().map(NoticeListDto::new).collect(Collectors.toList());
    }

    // Map 에서 보여줄 공지 2개
    @Override
    public List<NoticeListDto> getNoticesForMap(Store store) {
        return noticeRepository.findNoticesForMap(store)
                .stream().map(NoticeListDto::new)
                .collect(Collectors.toList());
    }

    // 사장님 마이페이지 공지사항 리스트 출력
    // no-offset 방식 무한스크롤
    @Override
    public Slice<NoticeListDto> getNoticesByOwner(int typeKey, String keyword, Long last, HttpServletRequest request) {
        User owner = userService.findUserByToken(request);
        Store store = storeRepository.findByUser(owner);
        NoticeType type = NoticeType.findWithKey(typeKey);

        if (store == null)
            throw new NotFoundException("존재하지 않는 가게", NOT_FOUND_EXCEPTION);
        if (owner == null || owner != store.getUser())
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        Pageable pageable = PageRequest.ofSize(10);

        List<NoticeBoard> list = noticeRepository.findNoticesByStore(type, keyword, last, store, pageable);

        return transType(list, pageable);
    }

    private Slice<NoticeListDto> transType(List<NoticeBoard> list, Pageable pageable) {
        boolean hasNext = false;

        if (list.size() > pageable.getPageSize()) {
            hasNext = true;
            list.remove(pageable.getPageSize());
        }
        Slice<NoticeBoard> slice = new SliceImpl<>(list, pageable, hasNext);
        return slice.map(NoticeListDto::new);
    }

    @Override
    public NoticeRequestDto getNoticeById(Long noticeId, HttpServletRequest request) {
        User owner = userService.findUserByToken(request);
        NoticeBoard notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공지", NOT_FOUND_EXCEPTION));

        if (owner == null || owner != notice.getStore().getUser()) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }

        return new NoticeRequestDto(notice);
    }



    @Override
    public void createNotice(NoticeRequestDto requestDto,
                             HttpServletRequest request) {
        User user = userService.findUserByToken(request);

        if (user == null)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        Store store = storeRepository.findByUser(user);
        if (store == null)
            throw new NotFoundException("존재하지 않는 가게", NOT_FOUND_EXCEPTION);

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

        if (notice == null)
            throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        if (notice.getStore().getUser() != user)
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);

        return notice;
    }
}
