package com.dessert.gallery.repository.Calendar;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CalendarRepositoryImpl implements CalendarRepositoryCustom {

    private final CalendarRepository calendarRepository;
    private final JPAQueryFactory jpaQueryFactory;

}
