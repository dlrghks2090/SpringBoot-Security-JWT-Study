package com.cos.security1.controller;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // localhost:8080/
    // localhost:8080
    @GetMapping({"","/"})
    public String index() {

        // 머스테치 기본폴더 src/main/resources/
        // 뷰리졸버 설정 : tamplates(prefix), .mustache(suffisx) -> 생략 가능
        return "index";     // src/main/resources/tamplates/index.mustache
    }

    @GetMapping("/user")
    public @ResponseBody String User() {

        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String Admin() {

        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String Manager() {

        return "manager";
    }

    // 스프링 시큐리티가 해당 주소를 낚아채버린다. -> SecurityConfig 파일 생성 후 작동 안함.
    @GetMapping("/loginForm")
    public String LoginForm() {

        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String JoinForm() {

        return "joinForm";
    }

    @PostMapping("/join")
    public  String Join(User user) {
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
