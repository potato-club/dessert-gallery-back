package com.dessert.gallery.entity;

import com.dessert.gallery.dto.store.StoreRequestDto;
import com.dessert.gallery.dto.store.StoreUpdateDto;
import com.dessert.gallery.dto.store.map.StoreCoordinate;
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
    private String info;

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

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private File image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_uid", nullable = false)
    private User user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscribe> followers = new ArrayList<>();

    public Store(StoreRequestDto requestDto, StoreCoordinate coordinate, User user) {
        this.name = requestDto.getName();
        this.info = requestDto.getInfo();
        this.content = requestDto.getContent();
        this.address = requestDto.getAddress();
        this.longitude = coordinate.getLon();
        this.latitude = coordinate.getLat();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.score = 0.0;
        this.user = user;
    }

    public void setImage(File file) {
        this.image = file;
    }

    public void updateScore(Double score) {
        this.score = score;
    }

    public void updateStore(StoreUpdateDto updateDto) {
        if(updateDto.getName() != null)
            this.name = updateDto.getName();
        if(updateDto.getInfo() != null)
            this.info = updateDto.getInfo();
        if(updateDto.getContent() != null)
            this.content = updateDto.getContent();
        if(updateDto.getAddress() != null)
            this.address = updateDto.getAddress();
        if(updateDto.getPhoneNumber() != null)
            this.phoneNumber = updateDto.getPhoneNumber();
    }

    public void updateCoordinate(StoreCoordinate coordinate) {
        this.latitude = coordinate.getLat();
        this.longitude = coordinate.getLon();
    }

    public boolean checkOwner(User user) {
        return this.user == user;
    }
}
