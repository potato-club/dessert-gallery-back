package com.dessert.gallery.controller;

import com.dessert.gallery.dto.calendar.CalendarResponseDto;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreResponseDto;
import com.dessert.gallery.service.Interface.CalendarService;
import com.dessert.gallery.service.Interface.ScheduleService;
import com.dessert.gallery.service.Interface.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Tag(name = "Store Controller", description = "가게 API")
public class StoreController {
    private final StoreService storeService;
    private final CalendarService calendarService;
    private final ScheduleService scheduleService;

    @Operation(summary = "가게 정보 조회 API")
    @GetMapping("/{storeId}")
    public StoreResponseDto getStore(@PathVariable(name = "storeId") Long storeId) {
        return storeService.getStoreDto(storeId);
    }

    @Operation(summary = "가게 페이지 캘린더 조회 API")
    @GetMapping("/{storeId}/calendar")
    public ResponseEntity<CalendarResponseDto> getCalendar(
            @PathVariable(name = "storeId") Long storeId,
            @RequestParam(required = false, defaultValue = "0", value = "year") int year,
            @RequestParam(required = false, defaultValue = "0", value = "month") int month) {
        if(year == 0 && month == 0) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getDayOfMonth();
        }
        CalendarResponseDto responseDto = calendarService.getCalendarByStore(storeId, year, month);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "사장님 페이지 캘린더 조회 API")
    @GetMapping("/{storeId}/calendar/owner")
    public ResponseEntity<?> getOwnerCalendar(
            @PathVariable(name = "storeId") Long storeId,
            @RequestParam(required = false, defaultValue = "0", value = "year") int year,
            @RequestParam(required = false, defaultValue = "0", value = "month") int month,
            HttpServletRequest request) {
        if(year == 0 && month == 0) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getDayOfMonth();
        }
        CalendarResponseDto responseDto = calendarService.getOwnerCalendar(storeId, year, month, request);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "가게 생성 API")
    @PostMapping("")
    public ResponseEntity<String> createStore(@RequestPart StoreRequestDto requestDto,
                                              @RequestPart(required = false) List<MultipartFile> images,
                                              HttpServletRequest request) {
        storeService.createStore(requestDto, images, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("가게 생성 완료");
    }

    @Operation(summary = "가게 캘린더 스케줄 등록 API")
    @PostMapping("/{storeId}/schedule")
    public ResponseEntity<String> createSchedule(@PathVariable(name = "storeId") Long id,
                                                 @RequestBody ScheduleRequestDto requestDto,
                                                 HttpServletRequest request) {
        scheduleService.addSchedule(id, requestDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("등록 완료");
    }

    @Operation(summary = "가게 정보 수정 API")
    @PutMapping("/{storeId}")
    public ResponseEntity<String> updateStore(@PathVariable(name = "storeId") Long id,
                                              @RequestPart StoreRequestDto updateDto,
                                              @RequestPart List<MultipartFile> images,
                                              @RequestPart List<FileRequestDto> requestDto,
                                              HttpServletRequest request) {
        storeService.updateStore(id, updateDto, images, requestDto, request);
        return ResponseEntity.ok("가게 정보 수정 완료");
    }

    @Operation(summary = "가게 삭제 API")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable(name = "storeId") Long id, HttpServletRequest request) {
        storeService.removeStore(id, request);
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
