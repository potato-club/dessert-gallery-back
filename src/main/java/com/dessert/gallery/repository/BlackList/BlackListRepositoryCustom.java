package com.dessert.gallery.repository.BlackList;


import com.dessert.gallery.dto.blacklist.BlackListResponseDto;
import com.dessert.gallery.entity.Store;
import com.querydsl.jpa.impl.JPAQuery;

import java.util.List;

public interface BlackListRepositoryCustom {

    List<BlackListResponseDto> getBlackListInStore(int page, Store store);

    JPAQuery<Long> countByStoreAndDeletedIsFalse(Store store);
}
