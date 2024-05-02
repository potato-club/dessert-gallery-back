package com.dessert.gallery.entity;

import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.enums.NoticeType;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean deleted;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean exposed;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Store store;

    public NoticeBoard(NoticeRequestDto requestDto, Store store) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.exposed = requestDto.isExposed();
        this.type = NoticeType.findWithKey(requestDto.getTypeKey());
        this.store = store;
    }

    public void updateNotice(NoticeRequestDto updateDto) {
        this.title = updateDto.getTitle();
        this.content = updateDto.getContent();
        this.exposed = updateDto.isExposed();
        this.type = NoticeType.findWithKey(updateDto.getTypeKey());
    }

    public void deleteNotice() {
        this.deleted = true;
    }
}
