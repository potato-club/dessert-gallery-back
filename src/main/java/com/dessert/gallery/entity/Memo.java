package com.dessert.gallery.entity;

import com.dessert.gallery.dto.memo.MemoRequestDto;
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
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Calendar calendar;

    public Memo(MemoRequestDto memoDto, Calendar calendar) {
        this.content = memoDto.getContent();
        this.completed = false;
        this.dateTime = LocalDate.parse(memoDto.getYear() + memoDto.getMonth() + "01",
                DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
        this.calendar = calendar;
    }

    public Memo toggleMemo() {
        this.completed = !this.completed;
        return this;
    }
}
