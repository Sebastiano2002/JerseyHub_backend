package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    boolean existsByEmail(String email);
}
