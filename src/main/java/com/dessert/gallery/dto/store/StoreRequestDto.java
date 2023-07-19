package com.dessert.gallery.dto.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreRequestDto {

    @ApiModelProperty(value = "가게 이름")
    private String name;

    @ApiModelProperty(value = "가게 소개")
    private String content;

    @ApiModelProperty(value = "가게 위도")
    private double latitude;

    @ApiModelProperty(value = "가게 경도")
    private double longitude;

    @ApiModelProperty(value = "가게 주소")
    private String address;

    @ApiModelProperty(value = "가게 전화번호")
    private String phoneNumber;
}
