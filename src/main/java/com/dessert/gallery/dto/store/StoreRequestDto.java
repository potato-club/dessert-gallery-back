package com.dessert.gallery.dto.store;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreRequestDto {
    private String name;
    private String content;
    private String latitude;
    private String longitude;
    private String address;
    private String phoneNumber;
}
