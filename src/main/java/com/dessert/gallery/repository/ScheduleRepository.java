package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCalendarAndDateTimeBetweenAndType(Calendar calendar, LocalDateTime start,
                                                           LocalDateTime end, ScheduleType type);
    List<Schedule> findByCalendarAndDateTimeBetween(Calendar calendar, LocalDateTime start, LocalDateTime end);
}
