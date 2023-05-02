package com.dessert.gallery.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
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
    @JoinColumn(name = "stores")
    private Store store;
}
