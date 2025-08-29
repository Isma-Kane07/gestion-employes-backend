package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe,Long> {
    long countByDepartementId(Long departementId);
    List<Employe> findByEmployeChef_Id(Long chefId);
    Employe findByNoMatricule(String noMatricule);
    @Query("select e from Employe e where e.departement =:departmentId and e.grade in (:grades) ")
    List<Employe> findByDepartmentIdAndGrade(@Param("departmentId") Long departmentId,@Param("grades") List<String> grades);

}
