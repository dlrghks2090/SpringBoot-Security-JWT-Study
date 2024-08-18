package com.cos.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
