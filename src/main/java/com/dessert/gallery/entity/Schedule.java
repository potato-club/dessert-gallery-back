package com.dessert.gallery.entity;

import com.dessert.gallery.dto.schedule.ReservationRequestDto;
import com.dessert.gallery.dto.schedule.ScheduleRequestDto;
import com.dessert.gallery.enums.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@NoArgsConstructor
public class Schedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Calendar calendar;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ScheduleType type;

    @Column
    private Boolean completed;

    @Column
    private Boolean submitReview;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_uid")
    private User client;

    public Schedule(ScheduleRequestDto requestDto, Calendar calendar) {
        this.dateTime = LocalDate.parse(requestDto.getDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        this.type = ScheduleType.findWithKey(requestDto.getKey());
        this.calendar = calendar;
    }

    public Schedule(ReservationRequestDto requestDto, User client, Calendar calendar) {
        this.dateTime = requestDto.getDateTime();
        this.type = ScheduleType.RESERVATION;
        this.client = client;
        this.calendar = calendar;
        this.completed = false;
        this.submitReview = false;
    }

    public Schedule toggleSchedule() {
        this.completed = !this.completed;
        return this;
    }

    public void submitReview() {
        this.submitReview = true;
    }

    public void removeSchedule() {
        this.calendar.removeSchedule(this);
    }
}
