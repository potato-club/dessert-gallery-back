package com.dessert.gallery.entity;

import com.dessert.gallery.dto.notice.NoticeRequestDto;
import com.dessert.gallery.enums.NoticeType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean deleted;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    public NoticeBoard(NoticeRequestDto requestDto, Store store) {
        this.content = requestDto.getContent();
        this.type = NoticeType.findWithKey(requestDto.getTypeKey());
        this.store = store;
    }

    public void updateNotice(NoticeRequestDto updateDto) {
        this.content = updateDto.getContent();
        this.type = NoticeType.findWithKey(updateDto.getTypeKey());
    }

    public void deleteNotice() {
        this.deleted = true;
    }
}
