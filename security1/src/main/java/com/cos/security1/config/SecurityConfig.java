package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // IoC 빈(Bean)을 등록
@EnableWebSecurity  // 스프링 시큐리티 활성화 -> 스프링 시큐리티 필터가 스프링 필터 체인에 등록된다.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)    // secured 어노테이션 활성화, preAuthorize 와 postAuthorize 어노테이션 활성화
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
                        .requestMatchers("/user/**").authenticated()    // /user 페이지는 인증만 하면 들어갈 수 있는 페이지입니다.
                        .requestMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  // 해당 권한중 하나가 있어야 합니다.
                        .requestMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")   // ADMIN 권한이 필요합니다.
                        .anyRequest().permitAll()   // 이외의 요청에서는 모든 권한이 허용되어 접근이 가능합니다.
                )

                .formLogin(form -> form
                        .loginPage("/loginForm")
//                        .usernameParameter("username")  // html 파일의 username 부분의 명칭을 바꾸지 않는다면 따로 설정하지 않고 생략 가능하디.
                        .loginProcessingUrl("/login")   // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다. -> 컨트롤러에 /login 관련 코드를 만들지 않아도 된다.
                        .defaultSuccessUrl("/")     // /login 주소를 통한 접근시 로그인 성공 후 이동 페이지 -> 이외의 주소를 통한 접근시 로그인에 성공하면 그 페이지를 넣어줍니다.
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
