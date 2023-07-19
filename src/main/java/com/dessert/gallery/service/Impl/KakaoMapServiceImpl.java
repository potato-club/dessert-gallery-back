package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.map.StoreCoordinate;
import com.dessert.gallery.dto.store.map.StoreMapList;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.repository.StoreRepository;
import com.dessert.gallery.service.Interface.KakaoMapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class KakaoMapServiceImpl implements KakaoMapService {

    private final StoreRepository storeRepository;

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

            // JSON 파싱을 통해 x, y 좌표 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            double x = responseJson.path("documents").path(0).path("x").asDouble();
            double y = responseJson.path("documents").path(0).path("y").asDouble();

            return StoreCoordinate.builder().x(x).y(y).build();
        } else {
            throw new Exception("Request failed with status code: " + response.getStatusCodeValue());
        }
    }

    @Override
    public List<StoreMapList> getKakaoMapStoreList(Long id) {

        Store store = storeRepository.findById(id).orElseThrow();

        return null;
    }

    // 반경 내 가게 필터링 메서드
    private List<Store> filterStoresWithinRadius(List<Store> stores, double latitude, double longitude, int radius) {
        List<Store> storesWithinRadius = new ArrayList<>();

        for (Store store : stores) {
            double distance = calculateDistance(latitude, longitude, store.getLatitude(), store.getLongitude());

            if (distance <= radius) {
                storesWithinRadius.add(store);
            }
        }

        return storesWithinRadius;
    }

    // Haversine 공식을 사용하여 두 지점 간 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // 지구의 반지름 (단위: km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
