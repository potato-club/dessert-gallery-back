package com.dessert.gallery.dto.store;

import com.dessert.gallery.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
    private Long id;
    private String name;
    private String introduction;
    private String address;
    private String phoneNumber;

    public StoreResponseDto(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.introduction = store.getContent();
        this.address = store.getAddress();
        this.phoneNumber = store.getPhoneNumber();
    }
}
