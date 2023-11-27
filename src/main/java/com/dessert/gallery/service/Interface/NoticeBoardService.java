package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NoticeBoardService {
    List<NoticeListDto> getNoticesByStore(Long storeId);
    List<NoticeListDto> getNoticesByOwner(int type, HttpServletRequest request);
    void createNotice(NoticeRequestDto requestDto, HttpServletRequest request);
    void updateNotice(Long noticeId, NoticeRequestDto updateDto, HttpServletRequest request);
    void deleteNotice(Long noticeId, HttpServletRequest request);
}
