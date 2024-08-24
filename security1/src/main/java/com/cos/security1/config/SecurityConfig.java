package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // IoC 빈(Bean)을 등록
@EnableWebSecurity  // 스프링 시큐리티 활성화 -> 스프링 시큐리티 필터가 스프링  필터 체인에 등록된다.
public class SecurityConfig {

//    @Autowired
//    private PrincipalOauth2UserService principalOauth2UserService;
//
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        //http.csrf().disable();
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                        .requestMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")   // 로그인 Form Action URL
                        .defaultSuccessUrl("/")     // 로그인 성공 후 이동 페이지
                );
//
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")
//                        .userInfoEndpoint()
//                        .userService(principalOauth2UserService)
//                )
//                .loginPage("/login")
//                .userInfoEndpoint()
//                .userService(principalOauth2UserService);

        return http.build();
    }
}
