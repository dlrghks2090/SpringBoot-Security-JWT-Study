package com.cos.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  //  ioc로 등록
public class WebMvcConfig implements WebMvcConfigurer {

    // 뷰리졸버 머스테치 재설정
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver resolver = new MustacheViewResolver();
        resolver.setCharset("UTF-8");   // 인코딩은 utf-8
        resolver.setContentType("text/html;charset=UTF-8");     // 뷰리졸버에 보내는 파일의 형식은 html 파일이고 파일의 인코딩은 utf-8이다.
        resolver.setPrefix("classpath:/templates/");    // 파일의 경로
        resolver.setSuffix(".html");    // mustache 파일이 아니라 html파일을 인식하도록 한다.

        registry.viewResolver(resolver);    // 레지스터에 뷰리졸버 등록
    }
}
