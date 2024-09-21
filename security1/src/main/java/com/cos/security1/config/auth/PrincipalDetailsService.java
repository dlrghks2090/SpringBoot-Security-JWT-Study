package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
    PrincipalDetailsService는 Spring Security에서 인증을 처리하기 위한 핵심적인 클래스입니다.
    이 클래스는 로그인 요청이 들어올 때 UserDetailsService를 통해 사용자의 정보를 조회하고, 인증이 성공하면 해당 정보를 PrincipalDetails 객체로 반환합니다.

    요약
    1. 사용자가 /login 요청을 보내면 Spring Security는 자동으로 UserDetailsService의 loadUserByUsername() 메서드를 호출합니다.
    2. 이 메서드는 username을 사용해 데이터베이스에서 사용자 정보를 조회합니다.
    3. 조회된 사용자 정보를 기반으로 PrincipalDetails 객체를 생성해 Spring Security 세션에 저장하고, 로그인 처리를 완료합니다.
 */

// 자동으로 호출되는 이유: Spring Security 설정에서 .loginProcessingUrl("/login")으로 지정된 경로가 실행되면 UserDetailsService를 자동으로 찾아 실행하게 되며, 이때 이 클래스의 loadUserByUsername() 메서드가 호출됩니다.
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 스프링 IoC 컨테이너에 등록 되어 있는 빈의 loadUserByUsername 메서드가 실행된다. => 규칙이다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 시큐리티 Session(의 내부에 authentication(의 내부에 UserDetails))
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("username = " + username);

        User userEntity = userRepository.findByUsername(username);

        // 유저 정보를 찾았을때
        if (userEntity != null) {
            // 이 메서드는 성공적으로 종료되면 PrincipalDetails 객체가 반환되어 Spring Security의 세션에 저장됩니다.
            // PrincipalDetails는 UserDetails를 구현한 클래스이며, Spring Security가 인증 처리에 필요한 사용자 정보를 담고 있습니다.
            // 세션 구조: Session → Authentication → UserDetails (여기서 UserDetails는 PrincipalDetails로 구현).
            return new PrincipalDetails(userEntity);
        }
        // userEntity가 존재하지 않으면 null을 반환하여, Spring Security는 이를 통해 인증 실패로 처리합니다.
        return null;
    }
}
