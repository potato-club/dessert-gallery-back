package com.dessert.gallery.dto.store.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreMapList {

    @Schema(description = "가게 이름")
    private String storeName;

    @Schema(description = "가게 주소")
    private String storeAddress;

    @Schema(description = "평점")
    private double score;

    @Schema(description = "위도")
    private double latitude;

    @Schema(description = "경도")
    private double longitude;
}
