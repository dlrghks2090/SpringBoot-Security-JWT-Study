package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // IoC 빈(Bean)을 등록
@EnableWebSecurity  // 스프링 시큐리티 활성화 -> 스프링 시큐리티 필터가 스프링 필터 체인에 등록된다.
public class SecurityConfig {

    // 해당 메서드의 리턴되는 오브젝트를 스프링 IoC 컨테이너에 빈으로 등록한다.
    @Bean
    public BCryptPasswordEncoder encodePwd() {  // BCrypt 해싱 함수를 사용해 비밀번호를 인코딩해주는 메서드입니다.
        return new BCryptPasswordEncoder();
    }

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
                        .requestMatchers("/user/**").authenticated()    // /user 페이지는 인증해야 합니다.
                        .requestMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  // 해당 권한중 하나가 있어야 합니다.
                        .requestMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")   // ADMIN 권한이 필요합니다.
                        .anyRequest().permitAll()   // 이외의 요청에서는 모든 권한이 허용되어 접근이 가능합니다.
                )

                .formLogin(form -> form
                        .loginPage("/loginForm")
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
