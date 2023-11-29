package com.dessert.gallery.controller;

import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.dto.store.StoreOwnerResponseDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.service.Interface.CalendarService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Tag(name = "Store Controller", description = "가게 API")
public class StoreController {
    private final StoreService storeService;
    private final CalendarService calendarService;
    private final ScheduleService scheduleService;

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
    public CalendarResponseDto getOwnerCalendar(
            @RequestParam(required = false, defaultValue = "0", value = "year") int year,
            @RequestParam(required = false, defaultValue = "0", value = "month") int month,
            HttpServletRequest request) {
        if (year == 0 && month == 0) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }
        return calendarService.getOwnerCalendar(year, month, request);
    }

    @Operation(summary = "가게 캘린더 조회 API - 회원")
    @GetMapping("/{storeId}/calendar")
    public ResponseEntity<CalendarResponseDto> getCalendar(
            @PathVariable(name = "storeId") Long storeId,
            @RequestParam(required = false, defaultValue = "0", value = "year") int year,
            @RequestParam(required = false, defaultValue = "0", value = "month") int month) {
        if (year == 0 && month == 0) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }
        CalendarResponseDto responseDto = calendarService.getCalendarByStore(storeId, year, month);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "가게 생성 API")
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createStore(@Parameter(description = "가게 정보", content =
                                                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                  @RequestPart StoreRequestDto requestDto,
                                              @Parameter(description = "업로드 할 파일", content =
                                                @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                    @RequestPart(required = false) MultipartFile image,
                                              HttpServletRequest request) {
        storeService.createStore(requestDto, image, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("가게 생성 완료");
    }

    @Operation(summary = "가게 캘린더 스케줄 등록 API")
    @PostMapping(value = "/schedule")
    public ResponseEntity<String> createSchedule(@RequestBody ScheduleRequestDto requestDto,
                                                 HttpServletRequest request) {
        scheduleService.addSchedule(requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("등록 완료");
    }

    @Operation(summary = "가게 정보 수정 API")
    @PutMapping(value = "/{storeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateStore(@PathVariable(name = "storeId") Long id,
                                              @Parameter(description = "가게 정보", content =
                                                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                    @RequestPart StoreRequestDto updateDto,
                                              @Parameter(description = "추가할 이미지", content =
                                                @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                    @RequestPart(required = false) MultipartFile image,
                                              @Parameter(description = "원본 이미지 추가 / 삭제 여부", content =
                                                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                    @RequestPart(required = false) FileRequestDto requestDto,
                                              HttpServletRequest request) throws Exception {
        storeService.updateStore(id, updateDto, image, requestDto, request);
        return ResponseEntity.ok("가게 정보 수정 완료");
    }

    @Operation(summary = "가게 삭제 API")
    @DeleteMapping("")
    public ResponseEntity<String> deleteStore(HttpServletRequest request) {
        storeService.removeStore(request);
        return ResponseEntity.ok("가게 삭제 완료");
    }

    @Operation(summary = "가게 스케줄 삭제 API")
    @DeleteMapping("/schedules")
    public ResponseEntity<String> deleteSchedule(@RequestParam(value = "id") Long id,
                                                 HttpServletRequest request) {
        scheduleService.removeSchedule(id, request);
        return ResponseEntity.ok("스케줄 삭제 완료");
    }
}
