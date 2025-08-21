package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByEmployeId(Long employeId);
}