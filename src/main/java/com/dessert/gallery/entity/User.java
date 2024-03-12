package com.dessert.gallery.entity;

import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    private String uid;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column
    private LoginType loginType;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean deleted;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean emailOtp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @Builder
    public User(String uid, String email, String password, String nickname, UserRole userRole,
                LoginType loginType, boolean deleted, boolean emailOtp) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
        this.loginType = loginType;
        this.deleted = deleted;
        this.emailOtp = emailOtp;
    }

    public void update(UserUpdateRequestDto userDto) {
        this.nickname = userDto.getNickname();
        this.userRole = userDto.getUserRole();
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setEmailOtp(boolean emailOtp) { this.emailOtp = emailOtp; }

    public void addBookmark(Bookmark bookmark) {
        bookmarkList.add(bookmark);
    }

    public void removeBookmark(Bookmark bookmark) {
        bookmarkList.removeIf(b -> b == bookmark);
    }
}
