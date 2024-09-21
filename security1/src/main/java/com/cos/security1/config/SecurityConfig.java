package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 1. 코드받기(인증)  2. 엑세스 토큰(권한)   3. 사용자 프로필 정보를 가져옴  4-1. 가져온 정보를 토대로 회원 가입을 자동으로 진행시키기도 함.
// 4-2. 가져온 정보가 부족하다면 추가로 정보를 입력하는 과정을 거쳐서 회원 가입을 진행시켜야 함.

@Configuration  // IoC 빈(Bean)을 등록. 이 클래스가 스프링의 설정 클래스임을 나타냅니다.
@EnableWebSecurity  // 스프링 시큐리티 활성화 -> Spring Security의 기본 필터 체인이 스프링의 필터 체인에 등록됩니다.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)    // 메서드 레벨 보안을 활성화합니다. secured 어노테이션 활성화, preAuthorize 와 postAuthorize 어노테이션 활성화
public class SecurityConfig {

    // 이 서비스는 OAuth2 로그인 시 사용자 정보를 가져오고 처리하는 역할을 합니다.
    // 예를 들어, 구글 OAuth2 로그인 후 사용자 정보를 가져와 처리하는 후속 작업을 수행합니다.
    // principalOauth2UserService는 구글, 페이스북 등과 같은 OAuth2 인증 후 처리에 사용됩니다.
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // 해당 메서드의 리턴되는 오브젝트를 스프링 IoC 컨테이너에 빈으로 등록한다. -> 해당 메서드를 빈으로 등록해서 순환참조발생하여 CustomBCryptPasswordEncode 클래스를 만들어 빈으로 등록해 문제를 해결했다.
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {  // BCrypt 해싱 함수를 사용해 비밀번호를 인코딩해주는 메서드입니다.
//        return new BCryptPasswordEncoder();
//    }

//    @Autowired
//    private PrincipalOauth2UserService principalOauth2UserService;
//
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }

    // 이 메서드는 HttpSecurity 객체를 사용하여 보안 정책을 설정합니다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF(Cross-Site Request Forgery) 공격 방어 기능을 비활성화합니다.
        // 보통 REST API와 같이 CSRF 토큰을 별도로 사용하지 않는 경우 비활성화합니다.
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/user/**").authenticated()    // /user 페이지는 인증만 하면 들어갈 수 있는 페이지입니다.
                        .requestMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  // 해당 권한중 하나가 있어야 합니다.
                        .requestMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")   // ADMIN 권한이 필요합니다.
                        .anyRequest().permitAll()   // 이외의 요청에서는 모든 권한이 허용되어 접근이 가능합니다.
                )

                .formLogin(form -> form
                        .loginPage("/loginForm")    // 로그인 페이지: /loginForm 경로를 통해 사용자 정의 로그인 페이지를 사용.
//                        .usernameParameter("username")  // html 파일의 username 부분의 명칭을 바꾸지 않는다면 따로 설정하지 않고 생략 가능하디.
                        .loginProcessingUrl("/login")   // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다. -> 컨트롤러에 /login 관련 코드를 만들지 않아도 된다.
                        .defaultSuccessUrl("/")     // /login 주속로 로그인 성공 후 이동할 기본 페이지: 로그인 성공 후 기본적으로 루트 경로(/)로 리다이렉션. -> 이외의 주소를 통한 접근시 로그인에 성공하면 그 페이지를 넣어줍니다.
                )
//
                // OAuth2 로그인 페이지: /loginForm 경로를 사용.
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginForm")    // 구글 로그인 완료된 뒤의 후처리가 필요하다. // Tip. 코드를 받지 X, 엑세스토큰+사용자프로필 정보를 받는다.
                        .userInfoEndpoint(userinfoEndpoint -> userinfoEndpoint  // 사용자 정보 처리: OAuth2 인증이 성공한 후에 PrincipalOauth2UserService가 사용자 정보를 처리합니다.
                                .userService(principalOauth2UserService))
                );
//                .loginPage("/login")
//                .userInfoEndpoint()
//                .userService(principalOauth2UserService);

        // SecurityFilterChain: 이 메서드는 보안 필터 체인을 설정하고, 반환된 필터 체인은 스프링 컨테이너에서 관리됩니다.
        // 이를 통해 인증과 인가에 대한 전반적인 보안 정책이 적용됩니다.
        return http.build();
    }
}
