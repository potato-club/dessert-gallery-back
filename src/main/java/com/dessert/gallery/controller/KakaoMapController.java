package com.dessert.gallery.controller;

import com.dessert.gallery.dto.store.map.MapRequestDto;
import com.dessert.gallery.dto.store.map.StoreCoordinate;
import com.dessert.gallery.dto.store.map.StoreMapList;
import com.dessert.gallery.service.Interface.KakaoMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/kakaoMap")
@Tag(name = "Kakao Map Controller", description = "카카오 맵 API")
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;

    @Operation(summary = "도로명 주소 좌표 변환 API")
    @GetMapping("/address")
    public StoreCoordinate getKakaoCoordinate(@RequestParam String address) throws Exception {
        return kakaoMapService.getKakaoCoordinate(address);
    }

    @Operation(summary = "좌표 주변 가게 리스트 출력 API")
    @GetMapping("")
    public List<StoreMapList> getStoreListWithCoordinate(@ModelAttribute MapRequestDto requestDto) {
        return kakaoMapService.getStoreListWithCoordinate(requestDto.getLat(), requestDto.getLon(), requestDto.getRadius());
    }

    @Operation(summary = "특정 카페 기준 리스트 출력 API")
    @GetMapping("/{id}")
    public List<StoreMapList> getKakaoMapStoreList(@PathVariable Long id) {
        return kakaoMapService.getKakaoMapStoreList(id);
    }
}
