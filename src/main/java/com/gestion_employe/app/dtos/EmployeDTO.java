package com.gestion_employe.app.dtos;

import lombok.AllArgsConstructor; // Utile pour un constructeur avec tous les champs par défaut
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList; // Importez ArrayList pour le constructeur personnalisé
import java.util.List;

@Data
@NoArgsConstructor
// @AllArgsConstructor peut être laissé ou retiré si vous préférez n'utiliser que le constructeur personnalisé
public class EmployeDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String noMatricule;
    private String tel;
    private Long departementId;
    private Long employeChefId;
    private List<String> roles;
    private boolean isChef; // <--- C'est la propriété manquante !


    public EmployeDTO(Long id, String nom, String prenom, String noMatricule, String tel, Long departementId, Long employeChefId, List<String> roles, boolean isChef) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.noMatricule = noMatricule;
        this.tel = tel;
        this.departementId = departementId;
        this.employeChefId = employeChefId;
        this.roles = (roles != null) ? roles : new ArrayList<>(); // Assure que roles est une liste vide si null
        this.isChef = isChef;
    }
}
