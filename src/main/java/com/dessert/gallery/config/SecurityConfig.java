package com.dessert.gallery.config;

import com.dessert.gallery.error.security.WebAccessDeniedHandler;
import com.dessert.gallery.jwt.JwtAuthenticationTokenFilter;
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
                .antMatchers(HttpMethod.GET, "/users/reissue").permitAll()
                .antMatchers(HttpMethod.GET, "/users/duplication/nickname").permitAll()
                .antMatchers("/users/login/**").permitAll()
                .antMatchers(HttpMethod.POST, "/users/mail/**").permitAll()
                // 정보수정 및 회원탈퇴는 권한이 필요하도록 설정
                .antMatchers(HttpMethod.PUT,"/users/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // 로그아웃, 토큰 재발급, 마이페이지 API 또한 권한이 필요하도록 설정
                .antMatchers(HttpMethod.GET,"/users/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // 메일 인증 관련 API 는 권한 없어도 접근할 수 있도록 설정
                .antMatchers(HttpMethod.POST,"/users/mail/**").permitAll()
                // 가게 관련 기능
                .antMatchers(HttpMethod.POST,"/reviews/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .antMatchers(HttpMethod.POST,"/stores/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.PUT,"/stores/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,"/stores/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/stores/{storeId}/calendar/owner").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/**/bookmark").hasAnyAuthority("USER")
                .antMatchers(HttpMethod.POST,"/boards/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.PUT,"/boards/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,"/boards/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.POST,"/notices/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.PUT,"/notices/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,"/notices/**").hasAnyAuthority("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/comments/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/comments/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                // 카카오맵, 1대1 채팅과 같은 기능들 권한 설정
                .antMatchers(HttpMethod.POST,"/mypage/room/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,"/mypage/room/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/mypage/room/**").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/kakaoMap").permitAll()
                .antMatchers("/ws/chat").authenticated()
                // 나머지 요청에 대해서는 권한 제한 없이 호출 가능하도록 설정
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
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
