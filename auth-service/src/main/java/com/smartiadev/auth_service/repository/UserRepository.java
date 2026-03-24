package com.smartiadev.auth_service.repository;

import com.smartiadev.auth_service.entity.User;
import jakarta.transaction.Transactional;
import org.hibernate.sql.results.graph.FetchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    Long countActiveUsers();

    List<User> findByRolesContaining(String role);

   /* @Modifying
    @Transactional
    @Query("update User u set u.enabled = true where u.enabled = false")
    void enableAllDisabledUsers();*/

}

