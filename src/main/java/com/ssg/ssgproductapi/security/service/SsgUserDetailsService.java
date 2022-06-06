package com.ssg.ssgproductapi.security.service;


import com.ssg.ssgproductapi.domain.User;
import com.ssg.ssgproductapi.repository.UserRepository;
import com.ssg.ssgproductapi.security.dto.AuthUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class SsgUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // username으로 email을 사용합니다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Login in progress..................");
        log.info("InstaUserDetailsService loadUserByUsername " + username);

        User user = userRepository.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException("Check Email: " + username));

        log.info("-----------------------------");
        log.info(user.toString());

        return new AuthUserDTO(
                user.getEmail(),
                user.getPassword(),
                user.getRoleSet().stream()
                        .map(role-> new SimpleGrantedAuthority
                                ("ROLE_"+role.name())).collect(Collectors.toSet()),
                user.getId()
        );
    }
}