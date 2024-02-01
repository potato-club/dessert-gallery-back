package com.dessert.gallery.entity;

import com.dessert.gallery.dto.user.request.UserUpdateRequestDto;
import com.dessert.gallery.enums.LoginType;
import com.dessert.gallery.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
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
