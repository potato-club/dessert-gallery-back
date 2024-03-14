package com.dessert.gallery.repository.StoreList;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;

import java.util.List;

public interface StoreListRepositoryCustom {

    List<StoreListResponseDto> getStoreListWithUser(String email, StoreSearchDto storeSearchDto);

    List<StoreListResponseDto> getStoreListWithGuest(StoreSearchDto storeSearchDto);

    List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto);

    List<ReviewListDto> getRecentReviewsSubQuery(Long storeId);
}
