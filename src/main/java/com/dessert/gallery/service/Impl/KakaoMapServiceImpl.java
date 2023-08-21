package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.map.MapSearchRequest;
import com.dessert.gallery.dto.store.map.StoreCoordinate;
import com.dessert.gallery.dto.store.map.StoreListInMap;
import com.dessert.gallery.dto.store.map.StoreMapList;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.SearchType;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.KakaoMapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class KakaoMapServiceImpl implements KakaoMapService {

    private final StoreRepository storeRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String GEOCODE_USER_INFO;

    private final String GEOCODE_URL = "http://dapi.kakao.com/v2/local/search/address.json?query=";

    @Override
    public StoreCoordinate getKakaoCoordinate(String address) throws Exception {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + GEOCODE_USER_INFO);
        headers.set("content-type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(GEOCODE_URL + encodedAddress, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();

            // JSON 파싱을 통해 lat, lon 좌표 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            double lat = responseJson.path("documents").path(0).path("lat").asDouble();
            double lon = responseJson.path("documents").path(0).path("lon").asDouble();

            return StoreCoordinate.builder().lat(lat).lon(lon).build();
        } else {
            throw new Exception("Request failed with status code: " + response.getStatusCodeValue());
        }
    }

    @Override
    public List<StoreMapList> getStoreListWithCoordinate(double lat, double lon, int radius) {
        QStore qStore = QStore.store;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                StoreMapList.class,
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
    public List<StoreMapList> getKakaoMapStoreList(Long id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Store ID"));

        final double lat = store.getLatitude();
        final double lon = store.getLongitude();
        final int radius = 1500;

        QStore qStore = QStore.store;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                StoreMapList.class,
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
    public List<StoreListInMap> getStoreListByTags(MapSearchRequest request) {
        BooleanBuilder whereBuilder = this.existsFilterOption(request);

        QStore qStore = QStore.store;
        QStoreBoard qStoreBoard = QStoreBoard.storeBoard;
        QFile qFile = QFile.file;

        return jpaQueryFactory.select(
                        Projections.constructor(
                                StoreListInMap.class,
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

    private BooleanBuilder existsFilterOption(MapSearchRequest request) {
        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (request.getAddress() != null) {
            whereBuilder.and(QStore.store.address.like("%" + request.getAddress() + "%"));
        }

        if (request.getSearchType().equals(SearchType.NAME)) {
            whereBuilder.and(QStore.store.name.like("%" + request.getKeyword() + "%"));
        } else if (request.getSearchType().equals(SearchType.TAGS)) {
            String[] keywordList = request.getKeyword().split("#");
            for (String option : keywordList) {
                whereBuilder.and(QStoreBoard.storeBoard.tags.like("%" + option + "%"));
            }
        } else {
            throw new UnAuthorizedException("401", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        return whereBuilder;
    }

    private BooleanExpression calculateDistance(double lat, double lon, int radius) {
        QStore qStore = QStore.store;
        NumberExpression<Double> distance = qStore.latitude.subtract(lon).multiply(qStore.latitude.subtract(lon))
                .add(qStore.longitude.subtract(lat).multiply(qStore.longitude.subtract(lat))).sqrt();

        return distance.loe(radius);
    }
}
