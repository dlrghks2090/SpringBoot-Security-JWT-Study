package com.cos.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View를 리턴한다.
public class IndexController {

    // localhost:8080/
    // localhost:8080
    @GetMapping({"","/"})
    public String index() {

        // 머스테치 기봄폴더 src/main/resources/
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
    @GetMapping("/login")
    public @ResponseBody String Login() {

        return "login";
    }

    @GetMapping("/join")
    public @ResponseBody String Join() {

        return "join";
    }

    @GetMapping("/joinProc")
    public @ResponseBody String JoinProc() {

        return "회원가입 완료됨";
    }

}
