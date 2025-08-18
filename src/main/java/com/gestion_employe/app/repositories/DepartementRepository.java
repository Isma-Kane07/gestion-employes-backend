package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartementRepository extends JpaRepository<Departement, Long> {
    @Query("SELECT COUNT(e) FROM Employe e WHERE e.departement.id = ?1")
    int countEmployesByDepartementId(Long departementId);
}
