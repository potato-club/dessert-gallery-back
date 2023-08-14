package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.S3Exception;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.NoticeBoardRepository;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import com.dessert.gallery.service.Interface.StoreService;
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

import static com.dessert.gallery.error.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NoticeBoardServiceImpl implements NoticeBoardService {
    private final NoticeBoardRepository noticeRepository;
    private final StoreService storeService;
    private final UserService userService;
    private final S3Service s3Service;

    @Override
    public NoticeResponseDto getNoticeById(Long noticeId) {
        NoticeBoard noticeBoard = noticeRepository.findByIdAndDeletedIsFalse(noticeId);
        if(noticeBoard == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        return new NoticeResponseDto(noticeBoard);
    }

    @Override
    public List<NoticeListDto> getNoticesByStore(Long storeId) {
        Store store = storeService.getStore(storeId);
        List<NoticeBoard> notices = noticeRepository.findByStoreAndDeletedIsFalse(store);
        if(notices == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        return notices.stream().map(NoticeListDto::new).collect(Collectors.toList());
    }

    @Override
    public void createNotice(Long storeId, NoticeRequestDto requestDto,
                             List<MultipartFile> images, HttpServletRequest request) {
        Store store = storeService.getStore(storeId);
        User user = userService.findUserByToken(request);

        if(store.getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        NoticeBoard notice = new NoticeBoard(requestDto, store);
        NoticeBoard saveNotice = noticeRepository.save(notice);
        if(!images.isEmpty()) {
            List<File> files = saveImage(images, saveNotice);
            saveNotice.setImages(files);
        }
    }

    @Override
    public void updateNotice(Long noticeId, NoticeRequestDto updateDto,
                             List<MultipartFile> images, List<FileRequestDto> requestDto,
                             HttpServletRequest request) {
        NoticeBoard notice = validateNotice(noticeId, request);
        if(!images.isEmpty()) {
            List<File> files = updateImage(notice, images, requestDto);
            notice.setImages(files);
        }
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

        if(notice == null) throw new NotFoundException("게시물 없음", NOT_FOUND_EXCEPTION);
        if(notice.getStore().getUser() != user) {
            throw new UnAuthorizedException("401 권한 없음", NOT_ALLOW_WRITE_EXCEPTION);
        }
        return notice;
    }

    private List<File> saveImage(List<MultipartFile> images, NoticeBoard notice) {
        try {
            return s3Service.uploadImages(images, notice);
        } catch (IOException e) {
            throw new S3Exception("이미지 업로드 에러", RUNTIME_EXCEPTION);
        }
    }

    private List<File> updateImage(NoticeBoard notice, List<MultipartFile> images, List<FileRequestDto> requestDto) {
        try {
            return s3Service.updateFiles(notice, images, requestDto);
        } catch (IOException e) {
            throw new S3Exception("이미지 업데이트 에러", RUNTIME_EXCEPTION);
        }
    }
}
