package com.dessert.gallery.entity;

import com.dessert.gallery.dto.file.FileDto;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 512)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeBoard_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private NoticeBoard noticeBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeBoard_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StoreBoard storeBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewBoard_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReviewBoard reviewBoard;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_uid")
    private User user;

    public void update(FileDto requestDto) {
        this.fileName = requestDto.getFileName();
        this.fileUrl = requestDto.getFileUrl();
    }
}
