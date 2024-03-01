package com.dessert.gallery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "blacklist")
public class BlackList extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_uid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stores_id")
    private Store store;

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
