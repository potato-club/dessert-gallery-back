package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.store.map.StoreCoordinate;
import com.dessert.gallery.dto.store.map.StoreMapList;

import java.util.List;

public interface KakaoMapService {

    StoreCoordinate getKakaoCoordinate(String address) throws Exception;

    List<StoreMapList> getKakaoMapStoreList(Long id);
}
