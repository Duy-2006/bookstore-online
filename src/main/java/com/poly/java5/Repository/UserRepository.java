package com.poly.java5.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import com.poly.java5.Entity.User;
import com.poly.java5.Entity.UserRole;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}

