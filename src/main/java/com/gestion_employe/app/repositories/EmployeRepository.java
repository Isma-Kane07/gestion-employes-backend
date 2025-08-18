package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeRepository extends JpaRepository<Employe,Long> {
    long countByDepartementId(Long departementId);
}
