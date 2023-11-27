package com.dessert.gallery.repository;

import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Calendar findByStore_User(User user);
    Calendar findByStoreId(Long id);
}
