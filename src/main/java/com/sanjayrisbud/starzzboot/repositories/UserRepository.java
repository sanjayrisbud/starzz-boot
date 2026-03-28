package com.sanjayrisbud.starzzboot.repositories;

import com.sanjayrisbud.starzzboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String s);
}