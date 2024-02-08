package com.dessert.gallery.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreUpdateDto {
    @Schema(description = "가게 이름")
    private String name;

    @Schema(description = "가게 정보")
    private String info;

    @Schema(description = "가게 소개")
    private String content;

    @Schema(description = "가게 주소")
    private String address;

    @Schema(description = "가게 전화번호")
    private String phoneNumber;

    @Schema(description = "원본 이미지만 삭제 여부 (업데이트 작업이면 false 로 보내야 됨)")
    private boolean removeOriginImage;
}
