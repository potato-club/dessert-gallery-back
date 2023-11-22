package com.dessert.gallery.controller;

import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.dto.notice.NoticeResponseDto;
import com.dessert.gallery.service.Interface.NoticeBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
@Tag(name = "Notice Board Controller", description = "공지 게시판 API")
public class NoticeBoardController {
    private final NoticeBoardService noticeService;

    @Operation(summary = "가게의 공지/이벤트 조회 - 회원용")
    @GetMapping("/stores/{storeId}")
    public List<NoticeListDto> getNoticeBoardByStore(@PathVariable(name = "storeId") Long storeId) {
        return noticeService.getNoticesByStore(storeId);
    }

    @Operation(summary = "가게의 공지/이벤트 조회 - 사장님용")
    @GetMapping("/myStore")
    public List<NoticeListDto> getNoticeBoardByOwner(@RequestParam(value = "type", defaultValue = "2") int type,
                                                     HttpServletRequest request) {
        return noticeService.getNoticesByOwner(type, request);
    }

    @Operation(summary = "공지글 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNoticeById(@PathVariable(name = "noticeId") Long noticeId) {
        NoticeResponseDto dto = noticeService.getNoticeById(noticeId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "가게 공지글 작성")
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<String> createNoticeBoard(@PathVariable(name = "storeId") Long storeId,
                                                    @RequestBody NoticeRequestDto requestDto,
                                                    HttpServletRequest request) {
        noticeService.createNotice(storeId, requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("공지사항 생성 완료");
    }

    @Operation(summary = "가게 공지글 수정")
    @PutMapping("/{noticeId}")
    public ResponseEntity<String> updateNoticeBoard(@PathVariable(name = "noticeId") Long noticeId,
                                                    @RequestBody NoticeRequestDto updateDto,
                                                    HttpServletRequest request) {
        noticeService.updateNotice(noticeId, updateDto, request);
        return ResponseEntity.ok("공지사항 수정 완료");
    }

    @Operation(summary = "가게 공지글 삭제 상태로 변경")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<String> deleteNoticeBoard(@PathVariable(name = "noticeId") Long noticeId,
                                                    HttpServletRequest request) {
        noticeService.deleteNotice(noticeId, request);
        return ResponseEntity.ok("공지사항 삭제 완료");
    }
}
