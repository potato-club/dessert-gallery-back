package com.dessert.gallery.repository.ReviewBoard;

import com.dessert.gallery.entity.ReviewBoard;
import com.dessert.gallery.entity.Store;

import java.util.List;

public interface ReviewBoardRepositoryCustom {
    List<ReviewBoard> getReviewsForMap(Store store);
}
