package com.dessert.gallery.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreRequestDto {

    @Schema(description = "가게 이름")
    private String name;

    @Schema(description = "가게 소개")
    private String content;

    @Schema(description = "가게 주소")
    private String address;

    @Schema(description = "가게 전화번호")
    private String phoneNumber;
}
