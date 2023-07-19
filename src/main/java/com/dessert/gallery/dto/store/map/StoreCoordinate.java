package com.dessert.gallery.dto.store.map;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StoreCoordinate {

    @ApiModelProperty(value = "위도")
    private double x;

    @ApiModelProperty(value = "경도")
    private double y;
}
