package com.dessert.gallery.repository.Schedule;

import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

    List<Schedule> findByCalendarAndDateTimeBetweenAndTypeIsNot(Calendar calendar, LocalDateTime start,
                                                           LocalDateTime end, ScheduleType type);

    List<Schedule> findByCalendarAndDateTimeBetween(Calendar calendar, LocalDateTime start, LocalDateTime end);

    List<Schedule> findAllByCalendar_StoreAndClientAndCompletedIsFalse(Store store, User client);

    boolean existsByCalendarAndDateTimeAndType(Calendar calendar, LocalDateTime dateTime, ScheduleType type);
}
