package com.dessert.gallery.repository.KakaoMap;

import com.dessert.gallery.dto.store.map.MapSearchRequest;
import com.dessert.gallery.dto.store.map.StoreListInMap;
import com.dessert.gallery.dto.store.map.StoreMapList;
import com.dessert.gallery.entity.QFile;
import com.dessert.gallery.entity.QStore;
import com.dessert.gallery.entity.QStoreBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class KakaoMapRepositoryImpl implements KakaoMapRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StoreMapList> getStoreListWithCoordinate(double lat, double lon, int radius) {
        QStore qStore = QStore.store;
        QFile qFile = QFile.file;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                StoreMapList.class,
                                qStore.id.as("storeId"),
                                qStore.name.as("storeName"),
                                qStore.address.as("storeAddress"),
                                qStore.score.as("score"),
                                qStore.latitude.as("latitude"),
                                qStore.longitude.as("longitude"),
                                qStore.content.as("content"),
                                qFile.fileName.as("fileName"),
                                qFile.fileUrl.as("fileUrl")
                        )
                )
                .from(qStore)
                .leftJoin(qFile).on(qFile.store.eq(qStore))
                .where(calculateDistance(lat, lon, radius))
                .orderBy(QStore.store.score.desc())
                .limit(15)
                .fetch();
    }

    @Override
    public List<StoreMapList> getKakaoMapStoreList(double lat, double lon, int radius) {
        QStore qStore = QStore.store;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                StoreMapList.class,
                                qStore.id.as("storeId"),
                                qStore.name.as("storeName"),
                                qStore.address.as("storeAddress"),
                                qStore.score.as("score"),
                                qStore.latitude.as("latitude"),
                                qStore.longitude.as("longitude")
                        )
                )
                .from(qStore)
                .where(calculateDistance(lat, lon, radius))
                .orderBy(QStore.store.score.desc())
                .limit(15)
                .fetch();
    }

    @Override
    public List<StoreListInMap> getStoreListByTags(BooleanBuilder whereBuilder, MapSearchRequest request) {
        QStore qStore = QStore.store;
        QStoreBoard qStoreBoard = QStoreBoard.storeBoard;
        QFile qFile = QFile.file;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                StoreListInMap.class,
                                qStore.id.as("storeId"),
                                qStore.name.as("storeName"),
                                qStore.address.as("storeAddress"),
                                qStore.content.as("content"),
                                qStore.score.as("score"),
                                qStore.latitude.as("latitude"),
                                qStore.longitude.as("longitude"),
                                qFile.fileName.as("fileName"),
                                qFile.fileUrl.as("fileUrl")
                        )
                )
                .from(qStore)
                .leftJoin(qStoreBoard).on(qStoreBoard.store.eq(qStore))
                .leftJoin(qFile).on(qFile.store.eq(qStore))
                .where(whereBuilder)
                .distinct()
                .orderBy(request.isSortType() ? QStore.store.followers.size().desc() : QStore.store.score.desc())
                .offset((request.getPage() - 1) * 15L)
                .limit(15)
                .fetch();
    }

    private BooleanExpression calculateDistance(double lat, double lon, int radius) {
        QStore qStore = QStore.store;
        NumberExpression<Double> distance = qStore.latitude.subtract(lon).multiply(qStore.latitude.subtract(lon))
                .add(qStore.longitude.subtract(lat).multiply(qStore.longitude.subtract(lat))).sqrt();

        return distance.loe(radius);
    }
}
