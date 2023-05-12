package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
    @ApiModelProperty(value = "가게 id")
    private Long id;
    @ApiModelProperty(value = "가게 이름")
    private String name;
    @ApiModelProperty(value = "가게 소개")
    private String introduction;
    @ApiModelProperty(value = "가게 주소")
    private String address;
    @ApiModelProperty(value = "가게 전화번호")
    private String phoneNumber;
    @ApiModelProperty(value = "가게 프로필 이미지")
    private FileDto storeImage;
    @ApiModelProperty(value = "가게 게시글 개수")
    private int postCount;

    public StoreResponseDto(Store store, int postCount) {
        this.id = store.getId();
        this.name = store.getName();
        this.introduction = store.getContent();
        this.address = store.getAddress();
        this.phoneNumber = store.getPhoneNumber();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
        this.postCount = postCount;
    }
}
