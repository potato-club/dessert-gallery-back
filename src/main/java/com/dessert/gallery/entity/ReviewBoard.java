package com.dessert.gallery.entity;

import com.dessert.gallery.dto.review.ReviewRequestDto;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int score;

    @OneToMany(mappedBy = "reviewBoard", orphanRemoval = true)
    private List<File> images = new ArrayList<>();

    @Formula("(SELECT COUNT(*) FROM likes as l WHERE l.reviewBoard_id = id)")
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stores_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_uid")
    private User user;

    public ReviewBoard(ReviewRequestDto requestDto, Store store, User user) {
        this.content = requestDto.getContent();
        this.score = requestDto.getScore();
        this.store = store;
        this.user = user;
        this.likeCount = 0;
    }
}
