package com.gestion_employe.app.services;

import com.gestion_employe.app.dtos.MissionDTO;
import com.gestion_employe.app.entities.Employe;
import com.gestion_employe.app.entities.Mission;
import com.gestion_employe.app.repositories.EmployeRepository;
import com.gestion_employe.app.repositories.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private EmployeRepository employeRepository;

    private Mission toEntity(MissionDTO dto) {
        Mission mission = new Mission();
        mission.setId(dto.getId());
        mission.setLieu(dto.getLieu());
        mission.setDescription(dto.getDescription());
        mission.setDateDebut(dto.getDateDebut());
        mission.setDateFin(dto.getDateFin());
        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId())
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé!"));
            mission.setEmploye(employe);
        }
        return mission;
    }

    private MissionDTO toDTO(Mission mission) {
        return new MissionDTO(
                mission.getId(),
                mission.getLieu(),
                mission.getDescription(),
                mission.getDateDebut(),
                mission.getDateFin(),
                mission.getEmploye() != null ? mission.getEmploye().getId() : null
        );
    }

    public List<MissionDTO> getAllMissions() {
        return missionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MissionDTO createMission(MissionDTO dto) {
        Mission saved = missionRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    public MissionDTO getMissionById(Long id) {
        return missionRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public Page<MissionDTO> getAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable).map(this::toDTO);
    }

    public List<MissionDTO> getMissionsByEmploye(Long employeId) {
        return missionRepository.findByEmployeId(employeId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Nouvelle méthode pour un chef d'employé
    public List<MissionDTO> getMissionsForChef(Long chefId) {
        List<Employe> subordonnes = employeRepository.findByEmployeChef_Id(chefId);
        List<Mission> missions = new ArrayList<>();

        // Récupérer les missions du chef lui-même
        missions.addAll(missionRepository.findByEmployeId(chefId));

        // Récupérer les missions de chaque subordonné
        for (Employe subordonne : subordonnes) {
            missions.addAll(missionRepository.findByEmployeId(subordonne.getId()));
        }

        return missions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MissionDTO updateMission(Long id, MissionDTO dto) {
        return missionRepository.findById(id).map(existingMission -> {
            existingMission.setLieu(dto.getLieu());
            existingMission.setDescription(dto.getDescription());
            existingMission.setDateDebut(dto.getDateDebut());
            existingMission.setDateFin(dto.getDateFin());
            if (dto.getEmployeId() != null) {
                Employe newEmploye = employeRepository.findById(dto.getEmployeId())
                        .orElseThrow(() -> new RuntimeException("Nouvel employé non trouvé !"));
                existingMission.setEmploye(newEmploye);
            }
            Mission updated = missionRepository.save(existingMission);
            return toDTO(updated);
        }).orElse(null);
    }

    public boolean deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            return false;
        }
        missionRepository.deleteById(id);
        return true;
    }
}
