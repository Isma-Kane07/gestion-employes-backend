// Dans Employe.java
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
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String noMatricule;
    private String tel;

    @ManyToOne
    private Departement departement;

    @OneToMany (mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions;

    @ManyToOne
    @JoinColumn(name = "chef_id")
    private Employe employeChef;

    @OneToMany(mappedBy = "employeChef", cascade = CascadeType.ALL)
    private List<Employe> subordonnes;

    // Suppression du champ 'roles' car il appartient à l'entité User.
    // @ManyToMany(fetch = FetchType.EAGER)
    // @JoinTable( ... )
    // private List<Role> roles;

    public Employe (Long id, String nom, String prenom, String noMatricule,
                    String tel, Departement departement) {
        this.id  = id;
        this.nom = nom;
        this.prenom = prenom;
        this.noMatricule = noMatricule;
        this.tel = tel;
        this.departement = departement;
    }
}