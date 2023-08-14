package com.dessert.gallery.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {
    @Bean
    public GroupedOpenApi defaultApi() {
        Info info = new Info().title("디저트 갤러리 API").version("v0.1");

        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .displayName("All API")
                .addOpenApiCustomiser(api -> api.setInfo(info))
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        Info info = new Info().title("유저 & 인증/인가 API").version("v0.1");

        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/users/**")
                .displayName("Users and Authorization")
                .addOpenApiCustomiser(api -> api.setInfo(info))
                .build();
    }

    @Bean
    public GroupedOpenApi storeApi() {
        Info info = new Info().title("가게 및 게시판 기능 API").version("v0.1");
        String[] paths = {"/stores/**", "/boards/**", "/notices/**", "/reviews/**"};

        return GroupedOpenApi.builder()
                .group("stores")
                .pathsToMatch(paths)
                .displayName("Store's API")
                .addOpenApiCustomiser(api -> api.setInfo(info))
                .build();
    }

    @Bean
    public GroupedOpenApi supportApi() {
        Info info = new Info().title("S3 및 Map API").version("v0.1");
        String[] paths = {"/kakaoMap/**", "/s3/**", "/mypage/room/**", "/mypage/follow/**"};

        return GroupedOpenApi.builder()
                .group("supports")
                .pathsToMatch(paths)
                .displayName("Support's API")
                .addOpenApiCustomiser(api -> api.setInfo(info))
                .build();
    }
}