package com.cos.security1.config.auth;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료되면 시큐리티  Session을 만들어 줍니다. (Security ContextHolder 라는 키값에 세션 정보를 저장)
// 시큐리티가 가지고 있는 세션에 들어갈 오브젝트 타입이  정해져 있다. => authentication 타입 객체
// authentication 안에 User 정보가 있어야 한다.
// User 오브젝트 타입 => UserDetails 타입 객체

// Security Session  (get)=>  Authentication  (get)=>  UserDetails(PrincipalDetails)

import com.cos.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
    PrincipalDetails 클래스는 Spring Security에서 일반 로그인과 OAuth2 로그인 모두를 처리할 수 있도록 UserDetails와 OAuth2User 인터페이스를 구현한 클래스입니다.
    즉, 이 클래스는 사용자가 어떤 방식으로 로그인하든 Spring Security에서 사용자 정보를 관리할 수 있게 해줍니다.

    UserDetails:
    이 인터페이스는 Spring Security에서 **일반 로그인(아이디/비밀번호 인증)**을 처리하기 위해 사용됩니다.
    이 인터페이스를 구현한 객체는 Authentication 객체 안에 저장되고, Authentication은 다시 Spring Security 세션에 저장됩니다.

    OAuth2User:
    이 인터페이스는 **OAuth2 로그인(구글, 페이스북 등)**을 처리하기 위해 사용됩니다.
    OAuth2 로그인 시, 외부 인증 제공자(구글, 네이버 등)로부터 받은 사용자 정보를 속성(attribute)으로 처리할 수 있습니다.
 */

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;  // 콤포지션
    private Map<String, Object> attributes;

    // 일반 로그인할때 사용하는 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인할때 사용하는 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 유저의 권한을 리턴하는 메서드
    // 여기서는 User 객체에서 역할(role)을 가져와서 Spring Security에서 요구하는 GrantedAuthority 객체로 변환하여 반환합니다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();   // 업캐스팅하여 자료형이 GrantedAuthority인 Collection의 자식인 ArrayList 생성
        collect.add(new GrantedAuthority() {    // user.getRole이 Stirng형이므로 오버라이드해서 값을 넣어준다.
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정의 만료 여부 리턴
    @Override
    public boolean isAccountNonExpired() {
        return true;    // 만료 안됨.
    }

    // 계정의 잠김 여부 리턴
    @Override
    public boolean isAccountNonLocked() {
        return true;    // 잠기지 않음
    }

    // 계정의 비밀번호 만료 여부 리턴
    @Override
    public boolean isCredentialsNonExpired() {
        return true;    // 만료 안됨
    }

    // 계정의 활성화 여부 리턴?
    @Override
    public boolean isEnabled() {

        // 해당 사이트에 1년동안 로그인 하지 않는다면 휴면 계정으로 변동
        // User에 TimStamp 자료형의 loginTime 변수를 생성하여 로그인 시각을 저장
        // 현재 시간 - 로그인 시 -> 1년을 초과하면 flase

        return true;    // 활성화됨
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
