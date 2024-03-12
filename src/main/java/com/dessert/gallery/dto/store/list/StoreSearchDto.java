package com.dessert.gallery.dto.store.list;

import com.dessert.gallery.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class StoreSearchDto {

    @Schema(description = "현재 페이지", defaultValue = "1")
    private int page;

    @Schema(description = "지역 필터링")
    private String address;

    @Schema(description = "검색 키워드 필터링")
    private String searchType;

    @NotNull
    @Schema(description = "최신 순 / 팔로워 순 / 평점 순", example = "RECENT / FOLLOWER / SCORE")
    private SearchType sortType;
}
