package com.dessert.gallery.controller;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.service.Interface.StoreListService;
import com.querydsl.core.Tuple;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/list")
@Api(tags = {"Store List Controller"})
public class StoreListController {

    private final StoreListService storeListService;

    @Operation(summary = "필터 적용/미적용 가게 목록 조회 API")
    @GetMapping("/stores")
    public List<StoreListResponseDto> getStoreList(@RequestBody StoreSearchDto storeSearchDto) {
        return storeListService.getStoreList(storeSearchDto);
    }

    @Operation(summary = "필터 적용/미적용 리뷰 목록 조회 API")
    @GetMapping("/reviews")
    public List<Tuple> getReviewList(@RequestBody StoreSearchDto storeSearchDto) {
        return storeListService.getReviewList(storeSearchDto);
    }
}
