package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 기본적인 CRUD 함수를 JPA레포지토리가 들고있다.
// @Repository 어노테이션이 없어도 스피링 IOC 컨테이너에 빈으로 등록이 된다. -> JPARepository를 상속받았기 때문에
public interface UserRepository extends JpaRepository<User, Integer> {

}
