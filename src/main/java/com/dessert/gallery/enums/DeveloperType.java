package com.dessert.gallery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeveloperType {

    Y("ROLE_Y", "JeongYunMi"),
    D("ROLE_D", "DonggyunKim00"),
    J("ROLE_J", "Joonhyung-Choi"),
    B("ROLE_B", "CJ-Park");

    private final String key;
    private final String title;
}
