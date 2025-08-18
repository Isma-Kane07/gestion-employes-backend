package com.gestion_employe.app.services;

import com.gestion_employe.app.dtos.DepartementDTO;
import com.gestion_employe.app.entities.Departement;
import com.gestion_employe.app.repositories.DepartementRepository;
import com.gestion_employe.app.repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartementService {

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private EmployeRepository employeRepository;

    private DepartementDTO toDTO(Departement departement) {
        return new DepartementDTO(departement.getId(), departement.getNomDepartment(), departement.getTaille());
    }

    private Departement toEntity(DepartementDTO dto) {
        return new Departement(dto.getId(), dto.getNomDepartment(), dto.getTaille());
    }

    public int getTailleActuelle(Long departementId) {
        return Math.toIntExact(employeRepository.countByDepartementId(departementId));
    }

    public List<DepartementDTO> getAllDepartements() {
        return departementRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<DepartementDTO> getAllDepartements(Pageable pageable) {
        return departementRepository.findAll(pageable).map(this::toDTO);
    }

    public DepartementDTO getDepartementById(Long id) {
        return departementRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public DepartementDTO createDepartement(DepartementDTO dto) {
        Departement saved = departementRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    public DepartementDTO updateDepartement(Long id, DepartementDTO dto) {
        return departementRepository.findById(id).map(existing -> {
            existing.setNomDepartment(dto.getNomDepartment());
            existing.setTaille(dto.getTaille());
            return toDTO(departementRepository.save(existing));
        }) .orElse(null);
    }

    public boolean deleteDepartement(Long id) {
        if (!departementRepository.existsById(id)) return false;
        departementRepository.deleteById(id);
        return true;
    }

}
