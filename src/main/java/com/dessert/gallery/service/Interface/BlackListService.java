package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.blacklist.BlackListRequestDto;
import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BlackListService {

    void addBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request);

    void removeBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request);

    Page<BlackListResponseDto> getBlackList(int page, HttpServletRequest request);

    void validateBlackList(Store store, User user);
}
