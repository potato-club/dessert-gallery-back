package com.dessert.gallery.repository.NoticeBoard;

import com.dessert.gallery.entity.NoticeBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.enums.NoticeType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeBoardRepositoryCustom {

    List<NoticeBoard> findNoticesForMap(Store store);
    List<NoticeBoard> findNoticesByStore(NoticeType type, String keyword, Long last,
                                            Store store, Pageable pageable);
}
