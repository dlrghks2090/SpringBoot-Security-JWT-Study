package com.cos.security1.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리를 하는 함수.
    // 여기서 가져온 Oauth 회원 정보로 강제 회원 가입을 시킬 것이다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration());   // registrationId로 어떤 OAuth로 로그인했는지 알 수 있다.
        System.out.println("getAccessToken  = " + userRequest.getAccessToken().getTokenValue());
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Client 라이브러리가 받음. ) -> Access Token 요청
        // userRequest 정보 -> loadUser()함수 호출 -> 구글로부터 회원 프로필 가져옴.
        System.out.println("getClientName = " + userRequest.getClientRegistration().getClientName());
        System.out.println("getAttributes = " + super.loadUser(userRequest).getAttributes());

        OAuth2User oauth2User = super.loadUser(userRequest);
        // 회원가입을 강제로 진행해 볼 예정.
        return super.loadUser(userRequest);
    }
}
