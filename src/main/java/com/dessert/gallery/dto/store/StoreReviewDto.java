package com.dessert.gallery.dto.store;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.dto.review.ReviewBoardResponseDto;
import com.dessert.gallery.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StoreReviewDto {
    private Long storeId;
    private String storeName;
    private String content;
    private FileDto storeImage;
    private List<ReviewBoardResponseDto> reviewList;

    public StoreReviewDto(Store store, List<ReviewBoardResponseDto> top2Reviews) {
        this.storeId = store.getId();
        this.storeName = store.getName();
        this.content = store.getContent();
        this.storeImage = store.getImage() == null ? null : new FileDto(store.getImage());
        this.reviewList = top2Reviews;
    }
}
