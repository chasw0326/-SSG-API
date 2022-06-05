package com.ssg.ssgproductapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SsgProductApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsgProductApiApplication.class, args);
    }

}
