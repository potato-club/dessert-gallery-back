package com.dessert.gallery.dto.store.list;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class StoreReviewDto {
    private Long storeId;
    private String storeName;
    private String content;
    private String fileName;
    private String fileUrl;
    private List<ReviewListDto> reviewList;

    public StoreReviewDto(Long storeId, String storeName, String content, String fileName, String fileUrl) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.content = content;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
