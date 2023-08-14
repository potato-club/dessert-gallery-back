package com.dessert.gallery.dto.store.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StoreCoordinate {

    @Schema(description = "위도")
    private double lat;

    @Schema(description = "경도")
    private double lon;
}
