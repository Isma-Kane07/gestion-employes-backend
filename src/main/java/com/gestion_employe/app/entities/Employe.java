// Dans Employe.java
package com.gestion_employe.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employe {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String noMatricule;
    private String tel;
    private String grade;

    @ManyToOne
    private Departement departement;

    @OneToMany (mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions;

    @ManyToOne
    @JoinColumn(name = "chef_id")
    private Employe employeChef;

    @OneToMany(mappedBy = "employeChef", cascade = CascadeType.ALL)
    private List<Employe> subordonnes;
    // Note: Le constructeur @AllArgsConstructor généré par Lombok prendra tous les champs,
    // y compris les listes et relations. Le constructeur personnalisé est plus spécifique.
    // Vous pouvez aussi ajouter un constructeur qui inclut employeChef si vous en avez besoin.
}