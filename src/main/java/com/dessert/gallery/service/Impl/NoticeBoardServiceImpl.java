package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.NoticeType;
import com.dessert.gallery.repository.NoticeBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import com.dessert.gallery.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NoticeBoardServiceImpl implements NoticeBoardService {
    private final NoticeBoardRepository noticeRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public NoticeResponseDto getNoticeById(Long noticeId) {
        NoticeBoard noticeBoard = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        if(noticeBoard == null) throw new RuntimeException("게시물 없음");
        return new NoticeResponseDto(noticeBoard);
    }

    @Override
    public List<NoticeListDto> getNoticesByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("store is not exist"));
        List<NoticeBoard> notices = noticeRepository.findByStoreAndDeletedIsFalse(store);
        if(notices == null) throw new RuntimeException("게시물 없음");
        return notices.stream().map(NoticeListDto::new).collect(Collectors.toList());
    }

    @Override
    public void createNotice(Long storeId, NoticeRequestDto requestDto, HttpServletRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        User user = userService.findUserByToken(request);

        if(store.getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        NoticeBoard notice = new NoticeBoard(requestDto, store);
        noticeRepository.save(notice);
    }

    @Override
    public void updateNotice(Long noticeId, NoticeRequestDto updateDto, HttpServletRequest request) {
        NoticeBoard notice = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        User user = userService.findUserByToken(request);
        if(notice == null) throw new RuntimeException("게시물 없음");
        if(notice.getStore().getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        notice.updateNotice(updateDto);
    }

    @Override
    public void deleteNotice(Long noticeId, HttpServletRequest request) {
        NoticeBoard notice = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        User user = userService.findUserByToken(request);

        if(notice == null) throw new RuntimeException("게시물 없음");
        if(notice.getStore().getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        notice.deleteNotice();
    }
}
