package com.gestion_employe.app.services;

import com.gestion_employe.app.dtos.EmployeDTO;
import com.gestion_employe.app.entities.Departement;
import com.gestion_employe.app.entities.Employe;
import com.gestion_employe.app.repositories.DepartementRepository;
import com.gestion_employe.app.repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private DepartementRepository departementRepository;

    private Employe toEntity(EmployeDTO dto) {
        Departement departement = departementRepository.findById(dto.getDepartementId()).orElse(null);
        return new Employe(dto.getId(), dto.getNom(), dto.getPrenom(), dto.getNoMatricule(), dto.getTel(), departement);
    }

    private EmployeDTO toDTO(Employe employe) {
        return new EmployeDTO(employe.getId(), employe.getNom(), employe.getPrenom(), employe.getNoMatricule(), employe.getTel(),
                employe.getDepartement() != null ? employe.getDepartement().getId() : null);
    }

    public List<EmployeDTO> getAllEmployes() {
        return employeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<EmployeDTO> getAllEmployes(Pageable pageable) {
        return employeRepository.findAll(pageable).map(this::toDTO);
    }

    public EmployeDTO getEmployeById(Long id) {
        return employeRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public EmployeDTO createEmploye(EmployeDTO dto) {
        Departement departement = departementRepository.findById(dto.getDepartementId()).orElseThrow(()-> new RuntimeException("introuvable"));

        long nbEmp = employeRepository.countByDepartementId(departement.getId());

        if (nbEmp >= departement.getTaille()) {
            throw new RuntimeException("Département complet !");
        }
        Employe saved = employeRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    public EmployeDTO updateEmploye(Long id, EmployeDTO dto) {
        return employeRepository.findById(id).map(existing -> {
            existing.setNom(dto.getNom());
            existing.setPrenom(dto.getPrenom());
            existing.setNoMatricule(dto.getNoMatricule());
            existing.setTel(dto.getTel());
            if(dto.getDepartementId() != null) {
                Departement departement = departementRepository.findById(dto.getDepartementId()).orElse(null);
                existing.setDepartement(departement);
            }
            return toDTO(employeRepository.save(existing));
        }).orElse(null);
    }

    public boolean deleteEmploye(Long id) {
        if (!employeRepository.existsById(id)) return false;
        employeRepository.deleteById(id);
        return true;
    }
}
