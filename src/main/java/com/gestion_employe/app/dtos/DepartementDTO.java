package com.gestion_employe.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartementDTO {
    private Long id;
    private String nomDepartment;
    private int taille;
}
