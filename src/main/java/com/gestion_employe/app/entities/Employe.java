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

public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String nom;
    private String prenom;
    private String noMatricule;
    private String tel;

    @ManyToOne
    private Departement departement;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions;

    public Employe(Long id, String nom, String prenom, String noMatricule, String tel, Departement departement) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.noMatricule = noMatricule;
        this.tel = tel;
        this.departement = departement;
    }
}
