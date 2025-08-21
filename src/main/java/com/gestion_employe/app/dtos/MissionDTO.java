package com.gestion_employe.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionDTO {
    private Long id;
    private String lieu;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long employeId;
}