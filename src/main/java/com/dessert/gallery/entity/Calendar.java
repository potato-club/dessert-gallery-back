package com.dessert.gallery.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Store store;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedule = new ArrayList<>();

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Memo> memoList = new ArrayList<>();

    public Calendar(Store store) {
        this.store = store;
    }

    public void addSchedule(Schedule schedule) {
        this.schedule.add(schedule);
    }
    public void removeSchedule(Schedule schedule) {
        this.schedule.remove(schedule);
    }
    public void addMemo(Memo memo) {
        this.memoList.add(memo);
    }
    public void removeMemo(Memo memo) {
        this.memoList.remove(memo);
    }
}
