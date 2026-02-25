package com.poly.java5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.poly.java5.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}