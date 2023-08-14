package com.dessert.gallery.dto.store.map;

import com.dessert.gallery.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapSearchRequest {

    @Schema(description = "지역 필터링")
    private String address;

    @Schema(description = "검색 키워드")
    private String keyword;

    @Schema(description = "검색 종류")
    private SearchType searchType;

    @Schema(description = "현재 페이지")
    private int page;

    @Schema(description = "팔로워 순 / 평점 순")
    private boolean sortType;
}
