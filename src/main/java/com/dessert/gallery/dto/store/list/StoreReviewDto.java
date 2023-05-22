package com.dessert.gallery.dto.store.list;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StoreReviewDto {
    private Long storeId;
    private String storeName;
    private String content;
    private String fileName;
    private String fileUrl;
    private List<ReviewListDto> reviewList;
}
