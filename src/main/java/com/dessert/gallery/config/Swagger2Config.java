package com.dessert.gallery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.dessert"})
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Config {

    @Bean(name = "defaultApi")
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(java.sql.Date.class)
                .forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dessert"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(userApiInfo())
                .enable(true)
                .host("localhost:8080");
    }

    @Bean(name = "userApi")
    public Docket userApi() {
        Predicate<String> path = PathSelectors.ant("/users/**");

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Users and Authorization")
                .select()
                .paths(path)
                .build()
                .apiInfo(userApiInfo());
    }

    @Bean(name = "shopApi")
    public Docket shopApi() {
        Predicate<String> path = PathSelectors.ant("/shop/**");

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Shop's Functions")
                .select()
                .paths(path)
                .build()
                .apiInfo(shopApiInfo());
    }

    private ApiInfo userApiInfo(){
        return new ApiInfoBuilder()
                .title("디저트 갤러리 유저 & 인증/인가 API")
                .description("API 상세소개 및 사용법")
                .version("1.0")
                .build();
    }

    private ApiInfo shopApiInfo(){
        return new ApiInfoBuilder()
                .title("디저트 갤러리 가게 기능 API")
                .description("API 상세소개 및 사용법")
                .version("1.0")
                .build();
    }
}