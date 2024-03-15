package com.dessert.gallery.repository.StoreBoard;

import com.dessert.gallery.entity.QStoreBoard;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.StoreBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreBoardRepositoryImpl implements StoreBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StoreBoard> findBoardsByStore(Store store, Pageable pageable) {
        BooleanBuilder whereQuery = new BooleanBuilder();
        whereQuery.and(QStoreBoard.storeBoard.deleted.isFalse());

        return jpaQueryFactory
                .select(QStoreBoard.storeBoard).from(QStoreBoard.storeBoard)
                .where(whereQuery.and(QStoreBoard.storeBoard.store.eq(store)))
                .orderBy(QStoreBoard.storeBoard.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1).fetch();
    }

    @Override
    public List<StoreBoard> findBoardsForChat(Store store, Long last, Pageable pageable) {
        BooleanBuilder whereQuery = new BooleanBuilder();
        whereQuery.and(QStoreBoard.storeBoard.store.eq(store)).and(QStoreBoard.storeBoard.deleted.isFalse());

        if (last != null) {
            whereQuery.and(QStoreBoard.storeBoard.id.lt(last));
        }

        return jpaQueryFactory.select(QStoreBoard.storeBoard).from(QStoreBoard.storeBoard)
                .where(whereQuery)
                .orderBy(QStoreBoard.storeBoard.createdDate.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    @Override
    public List<StoreBoard> findBoardsForMap(Store store) {
        return jpaQueryFactory.select(QStoreBoard.storeBoard).from(QStoreBoard.storeBoard)
                .where(QStoreBoard.storeBoard.store.eq(store).and(QStoreBoard.storeBoard.deleted.isFalse()))
                .orderBy(QStoreBoard.storeBoard.createdDate.desc())
                .limit(4).fetch();
    }
}
