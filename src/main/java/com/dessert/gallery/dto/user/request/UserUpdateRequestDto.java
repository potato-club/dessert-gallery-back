package com.dessert.gallery.dto.user.request;

import com.dessert.gallery.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "가게 주소")
    private String storeAddress;

    @ApiModelProperty(value = "가게 전화번호")
    private String storePhoneNumber;

    public User toEntity() {
        User user = User.builder()
                .nickname(nickname)
                .storeAddress(storeAddress)
                .storePhoneNumber(storePhoneNumber)
                .build();

        return user;
    }
}
