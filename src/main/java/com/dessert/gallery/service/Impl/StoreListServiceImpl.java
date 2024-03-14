package com.dessert.gallery.service.Impl;

import com.dessert.gallery.dto.store.StoreListResponseDto;
import com.dessert.gallery.dto.store.list.ReviewListDto;
import com.dessert.gallery.dto.store.list.StoreReviewDto;
import com.dessert.gallery.dto.store.list.StoreSearchDto;
import com.dessert.gallery.jwt.JwtTokenProvider;
import com.dessert.gallery.repository.StoreList.StoreListRepositoryCustom;
import com.dessert.gallery.service.Interface.StoreListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreListServiceImpl implements StoreListService {

    private final StoreListRepositoryCustom storeListRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public List<StoreListResponseDto> getStoreList(StoreSearchDto storeSearchDto, HttpServletRequest request) {

        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        if (accessToken != null) {
            String email = jwtTokenProvider.getUserEmail(accessToken);
            return storeListRepository.getStoreListWithUser(email, storeSearchDto);
        } else {
            return storeListRepository.getStoreListWithGuest(storeSearchDto);
        }
    }

    @Override
    public List<StoreReviewDto> getReviewList(StoreSearchDto storeSearchDto, HttpServletRequest request) {

        List<StoreReviewDto> storeReviewList = storeListRepository.getReviewList(storeSearchDto);

        for (StoreReviewDto storeReview : storeReviewList) {
            List<ReviewListDto> reviewList = storeListRepository.getRecentReviewsSubQuery(storeReview.getStoreId());
            storeReview.setReviewList(reviewList);
        }

        return storeReviewList;
    }
}
