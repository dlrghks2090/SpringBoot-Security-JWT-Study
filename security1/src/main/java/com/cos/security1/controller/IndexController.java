package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View를 리턴한다.
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;    // 비밀번호를 BCrypt 해싱 함수로 인코딩하기 위해 사용.

    // 세션 안에 시큐리티가 관리하는 시큐리티 세션이 따로 존재한다.
    // 이 시큐리티 세션 안에는 Authentication 객체가 들어있어서
    // 필요할때마다 컨트롤러에서 DI를 통해 꺼내 사용할 수 있다.

    // Authentication 안에는 두가지 형식의 타입이 들어갈 수 있다.
    // 첫번째는 UserDetails 타입(일반로그인)이고, 두번째는 OAuth2User 타입(OAuth로그인)이다.
    // 시큐리티 세션 안에 위 두 타입 중 하나의 객체가 들어가게 되면 로그인이 된 것이다.
    @GetMapping("/test/login")  // Authentication을 사용하거나 @AuthenticationPrincipal 를 사용해 타입지정 후 세션 정보에 접근할 수 있다.
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {  // DI (의존성 주입)
        System.out.println("/test/login =================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();   // 다운 캐스팅함.
        System.out.println("authentication = " + principalDetails.getUser());
        System.out.println("userDetails = " + userDetails.getUser());
        return "세션정보 확인하기";
    }

    // 일반 로그인과 oauth로그인의 다운캐스팅하는 객체형식이 다르기 때문에 따로 구성해주었다.
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) {  // DI (의존성 주입)
        System.out.println("/test/login =================");
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();     // 다운 캐스팅함.
        System.out.println("authentication = " + oauth2User.getAttributes());
        System.out.println("oauth2User = " + oauth.getAttributes());

        return "OAuth 세션정보 확인하기";
    }

    // localhost:8080/
    // localhost:8080
    @GetMapping({"","/"})
    public String index() {

        // 머스테치 기본폴더 src/main/resources/
        // 뷰리졸버 설정 : tamplates(prefix), .mustache(suffisx) -> 생략 가능
        return "index";     // src/main/resources/tamplates/index.mustache
    }

    // OAuth 로그인을 해도 PrincipalDetails
    // 일반 로그인을 해도 PrincipalDetails
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails : " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {

        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {

        return "manager";
    }

    // 스프링 시큐리티가 해당 주소를 낚아채버린다. -> SecurityConfig 파일 생성 후 작동 안함.
    @GetMapping("/loginForm")
    public String loginForm() {

        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {

        return "joinForm";
    }

    @PostMapping("/join")
    public  String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
//        userRepository.save(user);  // 회원가입 잘된다. But, 비밀번호가 1234 -> 시큐리티로 로그인을 할 수 없다. -> 패스워드가 암호화 되지 않았기 때문에
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 비밀번호 인코딩
        user.setPassword(encPassword);
        userRepository.save(user);  // 회원가입 잘된다. But, 비밀번호가 1234 -> 시큐리티로 로그인을 할 수 없다. -> 패스워드가 암호화 되지 않았기 때문에
        return  "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")  // 조건을 하나만 걸고 싶을때
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")    // 조건을 여러개 걸고 싶을때
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }

}
