package com.dessert.gallery.dto.store.map;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreMapList {

    @ApiModelProperty(value = "가게 이름")
    private String storeName;

    @ApiModelProperty(value = "가게 주소")
    private String storeAddress;

    @ApiModelProperty(value = "평점")
    private double score;

    @ApiModelProperty(value = "위도")
    private double latitude;

    @ApiModelProperty(value = "경도")
    private double longitude;
}
