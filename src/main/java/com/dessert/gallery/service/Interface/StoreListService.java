package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.querydsl.core.Tuple;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StoreListService {

    List<StoreListResponseDto> getStoreList(StoreSearchDto storeSearchDto, HttpServletRequest request);

    List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto, HttpServletRequest request);
}
