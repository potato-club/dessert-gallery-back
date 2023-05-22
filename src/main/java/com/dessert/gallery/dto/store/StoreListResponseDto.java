package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class StoreListResponseDto {

    @ApiModelProperty(value = "가게 ID")
    private Long id;
    @ApiModelProperty(value = "가게 이름")
    private String name;
    @ApiModelProperty(value = "가게 소개")
    private String content;
    @ApiModelProperty(value = "가게 주소")
    private String address;
    @ApiModelProperty(value = "가게 프로필 이미지 이름")
    private String fileName;
    @ApiModelProperty(value = "가게 프로필 이미지 URL")
    private String fileUrl;
    @ApiModelProperty(value = "가게 평균 점수")
    private Double score;

}
