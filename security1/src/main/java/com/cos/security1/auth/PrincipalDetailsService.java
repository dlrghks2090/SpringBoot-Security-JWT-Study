package com.cos.security1.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정(SecurityConfig)에서 .loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 스프링 IoC 컨테이너에 등록 되어 있는 빈의 loadUserByUsername 메서드가 실행된다. => 규칙이다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 시큐리티 Session(의 내부에 authentication(의 내부에 UserDetails))
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("username = " + username);

        User userEntity = userRepository.findByUsername(username);

        // 유저 정보를 찾았을때
        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        }
        // 유저 정보가 없을때
        return null;
    }
}
