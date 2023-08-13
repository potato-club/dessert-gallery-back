package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.follow.FollowResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FollowService {

    void addStoreFollowing(Long storeId, HttpServletRequest request);

    void removeStoreFollowing(Long storeId, HttpServletRequest request);

    List<FollowResponseDto> getFollowingList(int page, HttpServletRequest request);
}
