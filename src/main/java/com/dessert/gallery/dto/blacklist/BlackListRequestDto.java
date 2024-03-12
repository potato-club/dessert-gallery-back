package com.dessert.gallery.dto.blacklist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlackListRequestDto {

    @Schema(description = "가게 ID")
    private Long storeId;

    @Schema(description = "등록시킬 유저 닉네임")
    private String userName;
}
