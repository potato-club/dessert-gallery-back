package com.dessert.gallery.controller;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
@Api(tags = {"Notice Board Controller"})
public class NoticeBoardController {
    private final NoticeBoardService noticeService;

    // 가게 공지사항 리스트 조회
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<NoticeListDto>> getNoticeBoardByStore(@PathVariable(name = "storeId") Long storeId) {
        List<NoticeListDto> notices = noticeService.getNoticesByStore(storeId);
        return ResponseEntity.ok(notices);
    }

    // 가게 공지사항 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNoticeById(@PathVariable(name = "noticeId") Long noticeId) {
        NoticeResponseDto dto = noticeService.getNoticeById(noticeId);
        return ResponseEntity.ok(dto);
    }

    // 가게 공지사항 작성
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<String> createNoticeBoard(@PathVariable(name = "storeId") Long storeId,
                                                    @RequestBody NoticeRequestDto requestDto,
                                                    HttpServletRequest request) {
        noticeService.createNotice(storeId, requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("공지사항 생성 완료");
    }

    // 가게 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<String> updateNoticeBoard(@PathVariable(name = "noticeId") Long noticeId,
                                                    @RequestBody NoticeRequestDto updateDto,
                                                    HttpServletRequest request) {
        noticeService.updateNotice(noticeId, updateDto, request);
        return ResponseEntity.ok("공지사항 수정 완료");
    }

    // 가게 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<String> deleteNoticeBoard(@PathVariable(name = "noticeId") Long noticeId,
                                                    HttpServletRequest request) {
        noticeService.deleteNotice(noticeId, request);
        return ResponseEntity.ok("공지사항 삭제 완료");
    }
}
