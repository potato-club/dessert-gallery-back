package com.dessert.gallery.service.Interface;

import com.dessert.gallery.dto.blacklist.BlackListRequestDto;
import com.dessert.gallery.dto.blacklist.BlackListResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BlackListService {

    void addBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request);

    void removeBlackList(BlackListRequestDto blackListRequestDto, HttpServletRequest request);

    List<BlackListResponseDto> getBlackList(int page, HttpServletRequest request);
}
