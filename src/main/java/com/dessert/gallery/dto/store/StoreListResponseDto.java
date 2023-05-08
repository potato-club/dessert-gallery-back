package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreListResponseDto {
    private String name;
    private String content;
    private String address;
    private FileDto storeImage;
    private Double score;

    public StoreListResponseDto(Store store) {
        this.name = store.getName();
        this.content = store.getContent();
        this.address = store.getAddress();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
        this.score = store.getScore();
    }
}
