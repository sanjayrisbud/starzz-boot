package com.sanjayrisbud.starzzboot.repositories;

import com.sanjayrisbud.starzzboot.models.Constellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstellationRepository extends JpaRepository<Constellation, Integer> {
}