package com.dessert.gallery.entity;

import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.enums.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ScheduleType type;

    public Schedule(ScheduleRequestDto requestDto, Calendar calendar) {
        this.dateTime = LocalDate.parse(requestDto.getDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        this.type = ScheduleType.findWithKey(requestDto.getKey());
        this.calendar = calendar;
    }

    public void removeSchedule() {
        this.calendar.removeSchedule(this);
    }
}
