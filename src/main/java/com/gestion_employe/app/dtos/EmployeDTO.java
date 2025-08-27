package com.gestion_employe.app.dtos;

import com.gestion_employe.app.entities.Departement;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String noMatricule;
    private String tel;
    private Long departementId;
    private Long employeChefId;

    private List<String> roles;
}
