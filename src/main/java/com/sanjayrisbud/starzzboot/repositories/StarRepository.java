package com.sanjayrisbud.starzzboot.repositories;

import com.sanjayrisbud.starzzboot.models.Star;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarRepository extends JpaRepository<Star, Integer> {
}