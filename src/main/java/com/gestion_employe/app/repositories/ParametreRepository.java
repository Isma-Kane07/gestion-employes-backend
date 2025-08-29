package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    Optional<Parametre> findByKey(String key);
}
