package com.dessert.gallery.repository.Calendar;

import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Calendar findByStore_User(User user);
    Calendar findByStoreId(Long id);
}
