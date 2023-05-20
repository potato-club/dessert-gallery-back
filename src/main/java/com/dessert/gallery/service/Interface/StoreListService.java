package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.StoreSearchDto;

import java.util.List;

public interface StoreListService {

    List<StoreListResponseDto> getStoreList(StoreSearchDto storeSearchDto);
}
