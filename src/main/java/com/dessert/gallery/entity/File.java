package com.dessert.gallery.entity;

import com.dessert.gallery.dto.file.FileDto;
import lombok.*;

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

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noticeBoard")
    private NoticeBoard noticeBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewBoard")
    private ReviewBoard reviewBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeBoard")
    private StoreBoard storeBoard;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User")
    private User user;

    public void update(FileDto requestDto) {
        this.fileName = requestDto.getFileName();
        this.fileUrl = requestDto.getFileUrl();
    }
}
