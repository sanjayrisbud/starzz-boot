package com.sanjayrisbud.starzzboot.repositories;

import com.sanjayrisbud.starzzboot.models.Galaxy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalaxyRepository extends JpaRepository<Galaxy, Integer> {
}