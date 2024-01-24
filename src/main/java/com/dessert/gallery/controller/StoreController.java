package com.dessert.gallery.controller;

import com.dessert.gallery.dto.calendar.CalendarOwnerResponseDto;
import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.dto.memo.MemoRequestDto;
import com.dessert.gallery.dto.schedule.ReservationRequestDto;
import com.dessert.gallery.dto.schedule.ScheduleDetailResponseDto;
import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.dto.store.StoreOwnerResponseDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.service.Interface.CalendarService;
import com.dessert.gallery.service.Interface.MemoService;
import com.dessert.gallery.service.Interface.ScheduleService;
import com.dessert.gallery.service.Interface.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Tag(name = "Store Controller", description = "가게 API")
public class StoreController {
    private final StoreService storeService;
    private final CalendarService calendarService;
    private final ScheduleService scheduleService;
    private final MemoService memoService;

    @Operation(summary = "가게 정보 조회 API - 사장님 마이페이지")
    @GetMapping("")
    public StoreOwnerResponseDto getMyStore(HttpServletRequest request) {
        return storeService.getStoreDtoByUser(request);
    }

    @Operation(summary = "가게 정보 조회 API - 회원")
    @GetMapping("/{storeId}")
    public StoreResponseDto getStore(@PathVariable(name = "storeId") Long storeId,
                                     HttpServletRequest request) {
        return storeService.getStoreDto(storeId, request);
    }

    @Operation(summary = "가게 캘린더 조회 API - 사장님 마이페이지")
    @GetMapping("/calendar")
    public CalendarOwnerResponseDto getOwnerCalendar(
            @RequestParam(required = false, defaultValue = "0", value = "year") int year,
            @RequestParam(required = false, defaultValue = "0", value = "month") int month,
            HttpServletRequest request) {
        LocalDate now = LocalDate.now();

        if (year == 0) year = now.getYear();
        if (month == 0) month = now.getMonthValue();

        return calendarService.getOwnerCalendar(year, month, request);
    }

    @Operation(summary = "가게 캘린더 세부 스케줄 조회 API - 사장님 마이페이지")
    @GetMapping("/schedule")
    public ScheduleDetailResponseDto getDetailSchedule(
            @RequestParam(value = "year") int year,
            @RequestParam(value = "month") int month,
            @RequestParam(value = "day") int day,
            HttpServletRequest request) {
        return scheduleService.getDetailScheduleByDate(year, month, day, request);
    }

    @Operation(summary = "가게 휴무일 여부 조회 API - 사장님 마이페이지")
    @GetMapping("/closed")
    public boolean storeIsClosed(HttpServletRequest request) {
        LocalDate now = LocalDate.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), 0, 0);

        return scheduleService.getTodayIsHoliday(request, today);
    }

    @Operation(summary = "가게 캘린더 조회 API - 회원")
    @GetMapping("/{storeId}/calendar")
    public CalendarResponseDto getCalendar(
            @PathVariable(name = "storeId") Long storeId,
            @RequestParam(required = false, defaultValue = "0", value = "year") int year,
            @RequestParam(required = false, defaultValue = "0", value = "month") int month) {
        LocalDate now = LocalDate.now();

        if (year == 0) year = now.getYear();
        if (month == 0) month = now.getMonthValue();

        return calendarService.getCalendarByStore(storeId, year, month);
    }

    @Operation(summary = "가게 생성 API")
    @PostMapping(value = "",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> createStore(@Parameter(description = "가게 정보 - StoreRequestDto", content =
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                              @RequestPart StoreRequestDto requestDto,
                                              @Parameter(description = "업로드 할 이미지", content =
                                              @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                              @RequestPart(required = false) MultipartFile image,
                                              HttpServletRequest request) {
        storeService.createStore(requestDto, image, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("가게 생성 완료");
    }

    @Operation(summary = "가게 캘린더 스케줄 (휴무일 / 이벤트) 등록 API")
    @PostMapping(value = "/schedule")
    public ResponseEntity<String> createSchedule(@RequestBody ScheduleRequestDto requestDto,
                                                 HttpServletRequest request) {
        scheduleService.addSchedule(requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("스케줄 등록 완료");
    }

    @Operation(summary = "가게 픽업 예약 API")
    @PostMapping(value = "/reservation")
    public ResponseEntity<String> createReservation(@RequestBody ReservationRequestDto requestDto,
                                                    HttpServletRequest request) {
        scheduleService.addReservation(requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("픽업 예약 완료");
    }

    @Operation(summary = "가게 캘린더 메모 작성 API")
    @PostMapping(value = "/memo")
    public ResponseEntity<String> createSchedule(@RequestBody MemoRequestDto requestDto,
                                                 HttpServletRequest request) {
        memoService.addMemo(requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("메모 작성 완료");
    }

    @Operation(summary = "가게 정보 수정 API")
    @PutMapping(value = "/{storeId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateStore(@PathVariable(name = "storeId") Long id,
                                              @Parameter(description = "가게 정보 - StoreRequestDto", content =
                                              @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                              @RequestPart StoreRequestDto updateDto,
                                              @Parameter(description = "추가할 이미지", content =
                                              @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                              @RequestPart(required = false) MultipartFile image,
                                              HttpServletRequest request) throws Exception {
        storeService.updateStore(id, updateDto, image, request);
        return ResponseEntity.ok("가게 정보 수정 완료");
    }

    @Operation(summary = "가게 캘린더 메모 체크 API")
    @PutMapping("/memo")
    public ResponseEntity<String> checkMemo(@Parameter(description = "메모 id")
                                            @RequestParam(value = "id") Long id,
                                            HttpServletRequest request) {
        memoService.toggleMemo(id, request);
        return ResponseEntity.ok("메모 수정 완료");
    }

    @Operation(summary = "가게 삭제 API")
    @DeleteMapping("")
    public ResponseEntity<String> deleteStore(HttpServletRequest request) {
        storeService.removeStore(request);
        return ResponseEntity.ok("가게 삭제 완료");
    }

    @Operation(summary = "가게 스케줄 삭제 API")
    @DeleteMapping("/schedule")
    public ResponseEntity<String> deleteSchedule(@Parameter(description = "스케줄 id")
                                                 @RequestParam(value = "id") Long id,
                                                 HttpServletRequest request) {
        scheduleService.removeSchedule(id, request);
        return ResponseEntity.ok("스케줄 삭제 완료");
    }

    @Operation(summary = "가게 캘린더 메모 삭제 API")
    @DeleteMapping("/memo")
    public ResponseEntity<String> deleteMemo(@Parameter(description = "메모 id")
                                             @RequestParam(value = "id") Long id,
                                             HttpServletRequest request) {
        memoService.removeMemo(id, request);
        return ResponseEntity.ok("메모 삭제 완료");
    }
}
