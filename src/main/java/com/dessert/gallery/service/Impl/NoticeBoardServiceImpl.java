package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.repository.NoticeBoardRepository;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import com.dessert.gallery.service.Interface.UserService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
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
    private final S3Service s3Service;

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
    public void createNotice(Long storeId, NoticeRequestDto requestDto,
                             List<MultipartFile> images, HttpServletRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        User user = userService.findUserByToken(request);

        if(store.getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        NoticeBoard notice = new NoticeBoard(requestDto, store);
        if(!images.isEmpty()) {
            List<File> files = saveImage(images, notice);
            notice.setImages(files);
        }
        noticeRepository.save(notice);
    }

    @Override
    public void updateNotice(Long noticeId, NoticeRequestDto updateDto,
                             List<MultipartFile> images, HttpServletRequest request) {
        NoticeBoard notice = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        User user = userService.findUserByToken(request);
        if(notice == null) throw new RuntimeException("게시물 없음");
        if(notice.getStore().getUser() != user) {
            throw new RuntimeException("401 권한없음");
        }
        if(!images.isEmpty()) {
            List<File> files = updateImage(notice, images);
            notice.setImages(files);
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

    private List<File> saveImage(List<MultipartFile> images, NoticeBoard notice) {
        try {
            return s3Service.uploadImages(images, notice);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 에러");
        }
    }

    private List<File> updateImage(NoticeBoard notice, List<MultipartFile> images) {
        try {
            return s3Service.updateFiles(notice, images);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업데이트 에러");
        }
    }
}
