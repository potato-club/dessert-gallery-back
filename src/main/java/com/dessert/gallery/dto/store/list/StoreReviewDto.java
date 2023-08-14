package com.dessert.gallery.dto.store.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class StoreReviewDto {
    @Schema(description = "가게 ID")
    private Long storeId;
    @Schema(description = "가게 이름")
    private String storeName;
    @Schema(description = "게시글 내용")
    private String content;
    @Schema(description = "파일 이름")
    private String fileName;
    @Schema(description = "파일 URL")
    private String fileUrl;
    @Schema(description = "등록된 리뷰 리스트")
    private List<ReviewListDto> reviewList;

    public StoreReviewDto(Long storeId, String storeName, String content, String fileName, String fileUrl) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.content = content;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
