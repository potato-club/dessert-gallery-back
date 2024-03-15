package com.dessert.gallery.repository.StoreBoard;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreBoardRepositoryCustom {

    List<StoreBoard> findBoardsByStore(Store store, Pageable pageable);

    List<StoreBoard> findBoardsForChat(Store store, Long last, Pageable pageable);

    List<StoreBoard> findBoardsForMap(Store store);
}
