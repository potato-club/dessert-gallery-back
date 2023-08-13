package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Calendar findByStoreId(Long storeId);
}
