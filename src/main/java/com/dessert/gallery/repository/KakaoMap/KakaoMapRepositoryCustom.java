package com.dessert.gallery.repository.KakaoMap;

import com.dessert.gallery.dto.store.map.MapSearchRequest;
import com.dessert.gallery.dto.store.map.StoreListInMap;
import com.dessert.gallery.dto.store.map.StoreMapList;
import com.querydsl.core.BooleanBuilder;

import java.util.List;

public interface KakaoMapRepositoryCustom {

    List<StoreMapList> getStoreListWithCoordinate(double lat, double lon, int radius);

    List<StoreMapList> getKakaoMapStoreList(double lat, double lon, int radius);

    List<StoreListInMap> getStoreListByTags(BooleanBuilder whereBuilder, MapSearchRequest request);
}
