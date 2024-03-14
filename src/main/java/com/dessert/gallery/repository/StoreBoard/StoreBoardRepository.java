package com.dessert.gallery.repository.StoreBoard;

import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreBoardRepository extends JpaRepository<StoreBoard, Long>, StoreBoardRepositoryCustom {

    StoreBoard findByIdAndDeletedIsFalse(Long id);

    Long countAllByStoreAndDeletedIsFalse(Store store);
}
