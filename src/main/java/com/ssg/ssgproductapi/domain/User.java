package com.ssg.ssgproductapi.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "roleSet")
@Table(name = "users")
public class User extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 80)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<UserType> roleSet = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState userState;


}