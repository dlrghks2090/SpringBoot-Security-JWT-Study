package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
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

import java.util.Map;

/*
    이 코드는 Spring Security에서 OAuth2 소셜 로그인을 처리하는 서비스 클래스입니다.
    PrincipalOauth2UserService는 구글, 네이버 등과 같은 외부 OAuth2 제공자를 통해 로그인한 사용자의 정보를 처리하고, 필요할 경우 사용자를 데이터베이스에 자동으로 등록(회원가입)하는 역할을 합니다.

    OAuth2 로그인 프로세스:
    사용자가 구글이나 네이버 같은 OAuth2 제공자로 로그인하면, OAuth2 클라이언트 라이브러리가 먼저 인증을 수행한 후 Access Token을 받아옵니다.
    loadUser() 메서드는 super.loadUser(userRequest) 호출을 통해 해당 토큰을 사용하여 OAuth2 제공자(구글/네이버)로부터 사용자 프로필 정보를 가져옵니다.
    가져온 사용자 프로필 정보는 oauth2User.getAttributes()에서 확인할 수 있습니다.

    요약
    1. 사용자가 구글/네이버로 로그인하면 OAuth2 인증을 통해 사용자 프로필 정보를 가져옵니다.
    2. 사용자가 처음 로그인하는 경우, 제공된 OAuth2 정보로 자동 회원가입을 진행합니다.
    3. 이후 로그인 시 이미 존재하는 회원이라면, 기존 회원 정보를 사용하여 로그인을 처리합니다.
    4. 사용자의 정보는 PrincipalDetails 객체에 담겨 Spring Security의 인증 시스템으로 넘겨집니다.
 */

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
    // OAuth2UserRequest: OAuth2 인증을 위해 외부 인증 제공자(예: 구글, 네이버)에서 받은 정보가 포함되어 있으며, 인증이 완료된 후 이 요청이 loadUser() 메서드에서 처리됩니다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration());   // registrationId로 어떤 OAuth로 로그인했는지 알 수 있다.
        System.out.println("getAccessToken  = " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oauth2User = super.loadUser(userRequest);
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Client 라이브러리가 받음. ) -> Access Token 요청
        // userRequest 정보 -> loadUser()함수 호출 -> 구글로부터 회원 프로필 가져옴.
        System.out.println("getAttributes = " + oauth2User.getAttributes());

        // 회원가입을 강제로 진행해 볼 예정.
        // OAuth2UserInfo: 인터페이스로, 각 OAuth2 제공자(구글, 네이버 등)로부터 받은 사용자 정보의 구조가 다르기 때문에, 공통 인터페이스로 처리합니다.
        OAuth2UserInfo oAuth2UserInfo = null;

        // GoogleUserInfo, NaverUserInfo: 각각 구글과 네이버로부터 받은 사용자 정보를 해당하는 방식으로 파싱합니다.
        // registrationId를 사용해 사용자가 구글 로그인인지 네이버 로그인을 사용했는지 확인한 후, 각기 다른 파싱 방식으로 정보를 가져옵니다.
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        }
        else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            // naver는 response안에 유저 프로필 정보가 들어가 있어서 .get("response")까지 접근해줍니다.
            oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }
        else {
            System.out.println("우리는 구글 로그인과 네이버 로그인만 지원합니다.");
        }
        // 구글 로그인만 생각했을때의 코드입니다.
//        String provider = userRequest.getClientRegistration().getClientId();    // google
//        String providerId = oauth2User.getAttribute("sub"); // google provider id
//        String username = provider + "_" + providerId;  // google_115670172973792303639
//        String password = bCryptPasswordEncoder.encode("겟인데어"); // 크게 의미 없지만 채우기 위해 만듦.
//        String email = oauth2User.getAttribute("email");
//        String role = "ROLE_USER";

        // 업캐스팅을 사용하여 여러 OAuth 로그인을 적용시킨 코드입니다.
        // 어떤 도메인의 OAuth인지 상관없이 다 적용된다.
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;  // 구글이면 google_115670172973792303639, 네이버면 naver_7vCv6LhWKM7PBznen65Alm2dkd-uFoVCIMtF6xdY2z0
        String password = bCryptPasswordEncoder.encode("겟인데어"); // OAuth 로그인에서는 비밀번호는 의미가 없으나 필수 필드를 채우기 위해 기본 값을 넣습니다.
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";  // 기본적으로 ROLE_USER로 설정합니다.

        User userEntity = userRepository.findByUsername(username);

        // 회원 존재 여부 확인: 이미 같은 username이 존재하면 새로 회원가입을 하지 않고, 기존 회원 정보를 사용하여 로그인 처리만 합니다.
        if (userEntity == null) {
            System.out.println("로그인이 최초입니다.");
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
            System.out.println("로그인을 이미 한 적이 있습니다. 당신은 자동 회원 가입이 되어 있습니다.");
        }

        // OAuth2User: 인증이 성공하면 외부 제공자로부터 반환되는 사용자 프로필 정보입니다.
        // 일반 로그인과 OAuth2 로그인을 동일한 방식으로 처리하기 위해, 사용자 정보를 PrincipalDetails 객체에 담아서 반환합니다.
        // 이 반환된 PrincipalDetails 객체는 Spring Security의 Authentication 객체로 사용되며, 사용자 인증 정보를 포함합니다.
        System.out.println("oauth2User.getAttributes() : " + oauth2User.getAttributes());
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());    // 이것이 Authentication 객체 안에 들어간다.
    }
}
