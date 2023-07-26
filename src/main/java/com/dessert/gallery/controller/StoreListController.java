package com.dessert.gallery.controller;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.service.Interface.StoreListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/list")
@Tag(name = "Store List Controller", description = "가게 리스트 API")
public class StoreListController {

    private final StoreListService storeListService;

    @Operation(summary = "필터 적용/미적용 가게 목록 조회 API")
    @GetMapping("/stores")
    public List<StoreListResponseDto> getStoreList(@RequestBody StoreSearchDto storeSearchDto) {
        return storeListService.getStoreList(storeSearchDto);
    }

    @Operation(summary = "필터 적용/미적용 리뷰 목록 조회 API")
    @GetMapping("/reviews")
    public List<StoreReviewDto> getReviewList(@RequestBody StoreSearchDto storeSearchDto) {
        return storeListService.getReviewList(storeSearchDto);
    }
}
