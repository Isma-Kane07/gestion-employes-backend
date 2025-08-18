package com.gestion_employe.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String nomDepartment;
    private int taille;

    @OneToMany(mappedBy = "departement")
    private List<Employe> employes;

    public Departement(Long id, String nomDepartment, int taille) {
        this.id = id;
        this.nomDepartment = nomDepartment;
        this.taille = taille;
    }

}
