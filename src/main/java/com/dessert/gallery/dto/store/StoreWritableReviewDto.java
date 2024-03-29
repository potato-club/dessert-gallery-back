package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class StoreWritableReviewDto {
    @Schema(description = "가게 id")
    private Long id;

    @Schema(description = "가게 이름")
    private String name;

    @Schema(description = "가게 소개")
    private String content;

    @Schema(description = "가게 주소")
    private String address;

    @Schema(description = "가게 프로필 이미지")
    private FileDto storeImage;

    public StoreWritableReviewDto(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.content = store.getContent();
        this.address = store.getAddress();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
    }
}
