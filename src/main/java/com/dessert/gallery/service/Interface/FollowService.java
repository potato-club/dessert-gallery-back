package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.follow.FollowResponseDto;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

public interface FollowService {

    void addStoreFollowing(Long storeId, HttpServletRequest request);

    void removeFollowing(Long storeId, HttpServletRequest request);

    Page<FollowResponseDto> getFollowingList(int page, HttpServletRequest request);
}
