package com.dessert.gallery.dto.store.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreSearchDto {

    @Schema(description = "현재 페이지")
    private int page;

    @Schema(description = "지역 필터링")
    private String address;

    @Schema(description = "디저트 종류 필터링")
    private List<String> dessertType;

    @Schema(description = "검색 키워드 필터링")
    private String searchType;

    @Schema(description = "팔로워 순 / 평점 순")
    private boolean sortType;
}
