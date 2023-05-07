package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreReviewDto {
    private Long id;
    private String name;
    private FileDto storeImage;

    public StoreReviewDto(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
    }
}
