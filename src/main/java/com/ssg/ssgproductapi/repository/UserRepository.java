package com.ssg.ssgproductapi.repository;

import com.ssg.ssgproductapi.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 유저 찾기
     * @param email 이메일
     * @return 유저
     */
    @EntityGraph(attributePaths = {"userType"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u WHERE u.email =:email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 이메일 중복체크용
     * @param email 이메일
     * @return true, false
     */
    boolean existsByEmail(String email);
}
