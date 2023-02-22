package com.example.emailsender.repository;

import com.example.emailsender.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByEmail(String email);

        @Transactional
        @Modifying
        @Query("UPDATE User a SET a.enabled=true WHERE a.email=?1")
        int enableUser(String email);
    }

