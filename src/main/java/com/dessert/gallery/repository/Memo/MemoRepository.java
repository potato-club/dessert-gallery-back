package com.dessert.gallery.repository.Memo;

import com.dessert.gallery.entity.Calendar;
import com.dessert.gallery.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long>, MemoRepositoryCustom {

    List<Memo> findByCalendarAndDateTimeBetween(Calendar calendar, LocalDateTime start, LocalDateTime end);
}
