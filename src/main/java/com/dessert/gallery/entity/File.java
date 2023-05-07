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
    @JoinColumn(name = "noticeBoard_id")
    private NoticeBoard noticeBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeBoard_id")
    private StoreBoard storeBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewBoard_id")
    private ReviewBoard reviewBoard;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stores_id")
    private Store store;

    public void update(FileDto requestDto) {
        this.fileName = requestDto.getFileName();
        this.fileUrl = requestDto.getFileUrl();
    }
}
