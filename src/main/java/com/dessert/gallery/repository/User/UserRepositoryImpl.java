package com.dessert.gallery.repository.User;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;
}
