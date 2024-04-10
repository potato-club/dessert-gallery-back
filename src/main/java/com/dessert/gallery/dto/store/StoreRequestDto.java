package com.dessert.gallery.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class StoreRequestDto {

    @Schema(description = "가게 이름")
    @NotBlank(message = "가게 이름을 작성해주세요.")
    private String name;

    @Schema(description = "가게 정보")
    @NotBlank(message = "가게 정보를 작성해주세요.")
    private String info;

    @Schema(description = "가게 소개")
    @NotBlank(message = "가게 소개를 작성해주세요.")
    private String content;

    @Schema(description = "가게 주소")
    @NotBlank(message = "가게 주소를 작성해주세요.")
    private String address;

    @Schema(description = "가게 전화번호")
    private String phoneNumber;
}
