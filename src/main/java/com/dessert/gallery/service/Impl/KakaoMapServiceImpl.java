package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.board.BoardListResponseDtoForMap;
import com.dessert.gallery.dto.notice.NoticeListDto;
import com.dessert.gallery.dto.review.ReviewResponseDtoForMap;
import com.dessert.gallery.dto.store.map.*;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.enums.SearchType;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.KakaoMap.KakaoMapRepositoryCustom;
import com.dessert.gallery.repository.Store.StoreRepository;
import com.dessert.gallery.service.Interface.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.dessert.gallery.error.ErrorCode.NOT_FOUND_EXCEPTION;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoMapServiceImpl implements KakaoMapService {

    private final StoreRepository storeRepository;
    private final StoreBoardService boardService;
    private final NoticeBoardService noticeService;
    private final ReviewService reviewService;
    private final KakaoMapRepositoryCustom kakaoMapRepository;

    @Value("${spring.security.oauth2.client.registration.kakao-domain.client-id}")
    private String GEOCODE_USER_INFO;

    private static final String GEOCODE_URL = "http://dapi.kakao.com/v2/local/search/address.json?query=";

    @Override
    public StoreCoordinate getKakaoCoordinate(String address) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "KakaoAK " + GEOCODE_USER_INFO);
        headers.set("content-type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(GEOCODE_URL + address, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();

            // JSON 파싱을 통해 lat, lon 좌표 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            double lat = responseJson.path("documents").path(0).path("y").asDouble();
            double lon = responseJson.path("documents").path(0).path("x").asDouble();

            return StoreCoordinate.builder().lat(lat).lon(lon).build();
        } else {
            throw new Exception("Request failed with status code: " + response.getStatusCodeValue());
        }
    }

    @Override
    public List<StoreMapList> getStoreListWithCoordinate(double lat, double lon, int radius) {
        return kakaoMapRepository.getStoreListWithCoordinate(lat, lon, radius);
    }

    @Override
    public List<StoreMapList> getKakaoMapStoreList(Long id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Store ID"));

        final double lat = store.getLatitude();
        final double lon = store.getLongitude();
        final int radius = 1500;

        return kakaoMapRepository.getKakaoMapStoreList(lat, lon, radius);
    }

    @Override
    public List<StoreListInMap> getStoreListByTags(MapSearchRequest request) {
        BooleanBuilder whereBuilder = this.existsFilterOption(request);

        return kakaoMapRepository.getStoreListByTags(whereBuilder, request);
    }

    @Override
    public StoreDetailInMap getStoreDetailForMap(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EXCEPTION.getMessage(), NOT_FOUND_EXCEPTION));

        List<BoardListResponseDtoForMap> boards = boardService.getBoardsForMap(store);
        List<ReviewResponseDtoForMap> reviews = reviewService.getReviewsForMap(store);
        List<NoticeListDto> notices = noticeService.getNoticesForMap(store);

        return new StoreDetailInMap(store, boards, reviews, notices);
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
}
