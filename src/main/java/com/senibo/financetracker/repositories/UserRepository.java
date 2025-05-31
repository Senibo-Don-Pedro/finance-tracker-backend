package com.senibo.financetracker.repositories;

import com.senibo.financetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);


    Optional<User> findByUsernameOrEmail(String username, String email);


}
