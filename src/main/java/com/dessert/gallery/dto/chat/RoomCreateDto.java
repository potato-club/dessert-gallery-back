package com.dessert.gallery.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomCreateDto {

    @Schema(description = "가게 ID")
    private Long storeId;
}
