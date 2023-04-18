package com.dessert.gallery.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String password;
    private String nickname;
    private String storeAddress;
    private String storePhoneNumber;
}
