package com.dessert.gallery.repository.Schedule;

import com.dessert.gallery.entity.QSchedule;
import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Schedule> findSchedulesWritableReview(User client) {
        // store 가 같은 스케줄은 dateTime 이 제일 큰 스케줄만 가져와서 저장
        // 스케줄은 가장 최근에 픽업 완료된 순서대로 보여줌
        return jpaQueryFactory.select(QSchedule.schedule).from(QSchedule.schedule)
                .where(QSchedule.schedule.completed.isTrue().and(QSchedule.schedule.submitReview.isFalse())
                        .and(QSchedule.schedule.client.eq(client)))
                .groupBy(QSchedule.schedule.id, QSchedule.schedule.calendar.store)
                .having(QSchedule.schedule.dateTime.eq(
                        JPAExpressions.select(QSchedule.schedule.dateTime.max())
                                .from(QSchedule.schedule)
                                .where(QSchedule.schedule.calendar.store.eq(QSchedule.schedule.calendar.store))
                ))
                .orderBy(QSchedule.schedule.modifiedDate.desc())
                .fetch();
    }

    @Override
    public Schedule findRecentCompletedSchedule(Store store, User user) {
        return jpaQueryFactory.select(QSchedule.schedule).from(QSchedule.schedule)
                .where(QSchedule.schedule.completed.isTrue().and(QSchedule.schedule.submitReview.isFalse())
                        .and(QSchedule.schedule.client.eq(user).and(QSchedule.schedule.calendar.store.eq(store))))
                .orderBy(QSchedule.schedule.dateTime.desc())
                .fetchFirst();
    }
}
