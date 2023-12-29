package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.store.map.*;

import java.util.List;

public interface KakaoMapService {

    StoreCoordinate getKakaoCoordinate(String address) throws Exception;

    List<StoreMapList> getStoreListWithCoordinate(double lat, double lon, int radius);

    List<StoreMapList> getKakaoMapStoreList(Long id);

    List<StoreListInMap> getStoreListByTags(MapSearchRequest request);
    StoreDetailInMap getStoreDetailForMap(Long storeId);
}
