package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreListResponseDto {
    @ApiModelProperty(value = "가게 이름")
    private String name;
    @ApiModelProperty(value = "가게 내용")
    private String content;
    @ApiModelProperty(value = "가게 주소")
    private String address;
    @ApiModelProperty(value = "가게 프로필 이미지")
    private FileDto storeImage;
    @ApiModelProperty(value = "가게 평균 점수")
    private Double score;

    public StoreListResponseDto(Store store) {
        this.name = store.getName();
        this.content = store.getContent();
        this.address = store.getAddress();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
        this.score = store.getScore();
    }
}
