package com.dessert.gallery.repository;

import com.dessert.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByEmailAndDeleted(String email, boolean deleted);
    boolean existsByEmailAndDeletedAndEmailOtp(String email, boolean deleted, boolean emailOtp);
    List<User> findByDeletedIsTrueAndModifiedDateBefore(LocalDateTime localDateTime);
    boolean existsByEmailAndDeletedIsTrue(String email);
    boolean existsByEmailAndEmailOtpIsFalse(String email);
}
