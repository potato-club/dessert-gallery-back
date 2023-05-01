package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.entity.NoticeBoard;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NoticeBoardService {
    NoticeResponseDto getNoticeById(Long noticeId);
    List<NoticeListDto> getNoticesByStore(Long storeId);
    void createNotice(Long storeId, NoticeRequestDto requestDto, HttpServletRequest request);
    void updateNotice(Long noticeId, NoticeRequestDto updateDto, HttpServletRequest request);
    void deleteNotice(Long noticeId, HttpServletRequest request);
}
