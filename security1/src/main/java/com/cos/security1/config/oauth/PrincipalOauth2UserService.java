package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리를 하는 함수.
    // 여기서 가져온 Oauth 회원 정보로 강제 회원 가입을 시킬 것이다.

    // 재정의 하지 않아도 해당 함수는 알아서 실행이 되는데
    // 이 함수를 재정의한 이유는
    // 1. Oauth 회원정보로 강제 회원가입 하기위해서
    // 2. 반환 타입을 PrincipalDetails 으로 맞춰 일반 로그인과 묶어서 사용하기 위함이다.
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration());   // registrationId로 어떤 OAuth로 로그인했는지 알 수 있다.
        System.out.println("getAccessToken  = " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oauth2User = super.loadUser(userRequest);
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Client 라이브러리가 받음. ) -> Access Token 요청
        // userRequest 정보 -> loadUser()함수 호출 -> 구글로부터 회원 프로필 가져옴.
        System.out.println("getAttributes = " + oauth2User.getAttributes());

        // 회원가입을 강제로 진행해 볼 예정.
        String provider = userRequest.getClientRegistration().getClientId();    // google
        String providerId = oauth2User.getAttribute("sub"); // google provider id
        String username = provider + "_" + providerId;  // google_115670172973792303639
        String password = bCryptPasswordEncoder.encode("겟인데어"); // 크게 의미 없지만 채우기 위해 만듦.
        String email = oauth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            System.out.println("구글 로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }
        else {
            System.out.println("구글 로그인일 이미 한 적이 있습니다. 당신은 자동 회원 가입이 되어 있습니다.");
        }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());    // 이것이 Authentication 객체 안에 들어간다.
    }
}
