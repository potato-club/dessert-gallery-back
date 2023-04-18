package com.dessert.gallery.dto.user;

import com.dessert.gallery.entity.User;
import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDto {
    private String email;
    private String password;
    private String nickname;
    private LoginType loginType;

    public User toEntity() {
        User user = User.builder()
                .uid(String.valueOf(UUID.randomUUID()))
                .password(password)
                .email(email)
                .nickname(nickname)
                .userRole(UserRole.USER)
                .loginType(loginType)
                .deleted(false)
                .build();

        return user;
    }
}
