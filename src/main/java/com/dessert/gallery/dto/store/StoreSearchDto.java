package com.dessert.gallery.dto.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreSearchDto {

    @ApiModelProperty(value = "지역 필터링")
    private String address;

    @ApiModelProperty(value = "디저트 종류 필터링")
    private String dessertType;

    @ApiModelProperty(value = "검색 키워드 필터링")
    private String searchType;
}
