package com.ssg.ssgproductapi.config;


import com.ssg.ssgproductapi.security.filter.ApiCheckFilter;
import com.ssg.ssgproductapi.security.filter.ApiLoginFilter;
import com.ssg.ssgproductapi.security.handler.LoginFailHandler;
import com.ssg.ssgproductapi.security.service.SsgUserDetailsService;
import com.ssg.ssgproductapi.security.util.JWTUtil;
import com.ssg.ssgproductapi.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SsgUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.httpBasic().disable();
        http.formLogin().disable();
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
        http.rememberMe().tokenValiditySeconds(60 * 60 * 24 * 7).userDetailsService(userDetailsService);
        http.addFilterBefore(apiCheckFilter(),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiLoginFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

    private final String[] excludePaths =
            new String[]{
                    "/auth/signin", "/h2-console/**"};

    @Bean
    public ApiCheckFilter apiCheckFilter() {
        return new ApiCheckFilter("/**", excludePaths, jwtUtil());
    }

    @Bean
    public ValidateUtil validateUtil() {
        return new ValidateUtil();
    }

    @Bean
    public ApiLoginFilter apiLoginFilter() throws Exception {
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter
                ("/auth/signin", jwtUtil());
        apiLoginFilter.setAuthenticationManager(authenticationManager());
        apiLoginFilter.setAuthenticationFailureHandler(new LoginFailHandler());

        return apiLoginFilter;
    }
}
