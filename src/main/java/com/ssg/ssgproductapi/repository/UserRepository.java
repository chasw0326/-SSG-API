package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
