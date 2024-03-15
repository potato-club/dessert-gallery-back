package com.dessert.gallery.repository.Schedule;

import com.dessert.gallery.entity.Schedule;
import com.dessert.gallery.entity.Store;
import com.dessert.gallery.entity.User;

import java.util.List;

public interface ScheduleRepositoryCustom {
    List<Schedule> findSchedulesWritableReview(User client);

    Schedule findRecentCompletedSchedule(Store store, User user);
}
