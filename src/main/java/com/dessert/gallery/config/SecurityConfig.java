package com.dessert.gallery.config;

import com.dessert.gallery.error.security.AuthenticationEntryPointHandler;
import com.dessert.gallery.error.security.WebAccessDeniedHandler;
import com.dessert.gallery.jwt.JwtAuthenticationTokenFilter;
import com.dessert.gallery.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final WebAccessDeniedHandler webAccessDeniedHandler;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .cors()
                .and()
                .headers().frameOptions().disable()
                .and()
                .formLogin().disable()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .authorizeRequests()
                // 로그인, 회원가입은 토큰 없이도 호출 가능하도록 permitAll() 설정
                .antMatchers(HttpMethod.POST,"/users/signup").permitAll()
                .antMatchers(HttpMethod.GET,"/users/login/**").permitAll()
                .antMatchers(HttpMethod.POST, "/users/mail/**").permitAll()
                // 정보수정은 USER, MANAGER, ADMIN 권한이 필요하도록 설정
                .antMatchers(HttpMethod.PUT,"/users/**").hasAnyAuthority("USER", "MANAGER")
                // 로그아웃, 마이페이지 API 또한 권한이 필요하도록 설정
                .antMatchers(HttpMethod.GET,"/users/logout").hasAnyAuthority("USER", "MANAGER")
                .antMatchers(HttpMethod.GET,"/users").hasAnyAuthority("USER", "MANAGER")
                // 나머지 요청에 대해서는 권한 제한 없이 호출 가능하도록 설정
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointHandler)
                .accessDeniedHandler(webAccessDeniedHandler)
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
