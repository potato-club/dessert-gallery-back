package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import org.springframework.data.domain.Slice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NoticeBoardService {
    List<NoticeListDto> getNoticesByStore(Long storeId);
    Slice<NoticeListDto> getNoticesByOwner(int type, String keyword, Long last, HttpServletRequest request);
    NoticeRequestDto getNoticeById(Long noticeId, HttpServletRequest request);
    void createNotice(NoticeRequestDto requestDto, HttpServletRequest request);
    void updateNotice(Long noticeId, NoticeRequestDto updateDto, HttpServletRequest request);
    void deleteNotice(Long noticeId, HttpServletRequest request);
}
