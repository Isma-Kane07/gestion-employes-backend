package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe,Long> {
    long countByDepartementId(Long departementId);
    List<Employe> findByEmployeChef_Id(Long chefId);
    Employe findByNoMatricule(String noMatricule);
}
