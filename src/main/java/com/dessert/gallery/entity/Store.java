package com.dessert.gallery.entity;

import com.dessert.gallery.dto.store.StoreRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stores")
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column(nullable = false)
    private String address;

    @Column
    private String phoneNumber;

    @Column
    private double score;

    @OneToOne(mappedBy = "store", orphanRemoval = true)
    private File image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_uid", nullable = false)
    private User user;

    @OneToMany(mappedBy = "store", orphanRemoval = true)
    private List<Menu> menu = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscribe> followers = new ArrayList<>();

    public Store(StoreRequestDto requestDto, User user) {
        this.name = requestDto.getName();
        this.content = requestDto.getContent();
        this.address = requestDto.getAddress();
        this.longitude = requestDto.getLongitude();
        this.latitude = requestDto.getLatitude();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.score = 0.0;
        this.user = user;
    }

    public void setImage(File file) {
        this.image = file;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void updateStore(StoreRequestDto updateDto) {
        this.name = updateDto.getName();
        this.content = updateDto.getContent();
        this.address = updateDto.getAddress();
        this.latitude = updateDto.getLatitude();
        this.longitude = updateDto.getLongitude();
        this.phoneNumber = updateDto.getPhoneNumber();
    }
}
