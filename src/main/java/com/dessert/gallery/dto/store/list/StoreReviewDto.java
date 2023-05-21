package com.dessert.gallery.dto.store.list;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.File;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreReviewDto {
    private Long storeId;
    private String storeName;
    private String content;
    private String fileName;
    private String fileUrl;
}
