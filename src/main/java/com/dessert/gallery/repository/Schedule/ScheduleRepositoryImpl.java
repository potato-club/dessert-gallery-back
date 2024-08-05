package com.dessert.gallery.repository.Schedule;

import com.dessert.gallery.entity.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Schedule> findSchedulesWritableReview(User client) {
        // store 가 같은 스케줄은 dateTime 이 제일 큰 스케줄만 가져와서 저장
        // 스케줄은 가장 최근에 픽업 완료된 순서대로 보여줌
        QSchedule schedule = QSchedule.schedule;
        QCalendar calendar = QCalendar.calendar;
        QStore store = QStore.store;

        // 서브쿼리 - 각 store별로 가장 최근의 schedule dateTime을 조회
        QSchedule subSchedule = new QSchedule("subSchedule");
        JPAQuery<LocalDateTime> subQuery = new JPAQuery<LocalDateTime>()
                .select(subSchedule.dateTime.max())
                .from(subSchedule)
                .join(subSchedule.calendar, calendar)
                .where(subSchedule.client.eq(client)
                        .and(subSchedule.submitReview.isFalse())
                        .and(subSchedule.completed.isTrue())
                        .and(subSchedule.calendar.store.eq(store))
                )
                .groupBy(subSchedule.calendar.store);

        // 메인 쿼리 - 서브쿼리에서 조회한 dateTime과 일치하는 schedule을 필터링
        return jpaQueryFactory.selectFrom(schedule)
                .join(schedule.calendar, calendar)
                .join(calendar.store, store)
                .where(schedule.dateTime.in(subQuery)
                        .and(schedule.client.eq(client))
                        .and(schedule.submitReview.isFalse())
                        .and(schedule.completed.isTrue()))
                .orderBy(schedule.modifiedDate.desc())
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
