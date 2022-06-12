package com.ssg.ssgproductapi.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "userType")
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
    @JoinColumn(name = "user_type")
    private Set<UserType> userType = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState userState;

    public void addUserRole(UserType userType) {
        this.userType.add(userType);
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateUserState(UserState userState) { this.userState = userState; }

    public void updateName(String name) { this.name = name; }

    @Builder
    private User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userState = UserState.정상;
    }

}
