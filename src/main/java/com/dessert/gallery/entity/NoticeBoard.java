package com.dessert.gallery.entity;

import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.enums.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class NoticeBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private boolean deleted;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "noticeBoard", orphanRemoval = true)
    private List<File> file = new ArrayList<>();

    public NoticeBoard(NoticeRequestDto requestDto, Store store) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.type = NoticeType.findWithKey(requestDto.getTypeKey());
        this.store = store;
    }

    public void updateNotice(NoticeRequestDto updateDto) {
        this.title = updateDto.getTitle();
        this.content = updateDto.getContent();
        this.type = NoticeType.findWithKey(updateDto.getTypeKey());
    }

    public void deleteNotice() {
        this.deleted = true;
    }
}
