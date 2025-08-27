package com.gestion_employe.app.repositories;

import com.gestion_employe.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmployeId(Long employeId);

}
