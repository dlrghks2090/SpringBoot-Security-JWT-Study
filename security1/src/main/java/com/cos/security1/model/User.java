package com.cos.security1.model;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ORM - Object Relation Mapping

@Builder    // 빌더 패턴 적용
@Data       // @Getter, @Setter, @ToString, @EqualAndHashCode, @RequierdArgConstructor를 합쳐놓은 어노테이션이다.
@Entity     // JPA가 관리하도록 한다.
@NoArgsConstructor      // 매개변수가 하나도 없는 생성자
@AllArgsConstructor     // 모든 필드값을 매개변수로 갖는 생성자
public class User {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role; //ROLE_USER, ROLE_ADMIN
    // OAuth를 위해 구성한 추가 필드 2개
    private String provider;
    private String providerId;
    @CreationTimestamp  // 자동으로 날짜가 생성되어 입력된다.
    private Timestamp createDate;
}