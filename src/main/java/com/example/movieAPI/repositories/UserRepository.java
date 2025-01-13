package com.example.movieAPI.repositories;

import com.example.movieAPI.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String username); /// one object user can exist or not, avoid nullpointexception
}
