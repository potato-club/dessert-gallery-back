package com.dessert.gallery.dto.store.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapRequestDto {

    @Schema(description = "위도")
    private double lat;

    @Schema(description = "경도")
    private double lon;

    @Schema(description = "반경(m)")
    private int radius;
}
