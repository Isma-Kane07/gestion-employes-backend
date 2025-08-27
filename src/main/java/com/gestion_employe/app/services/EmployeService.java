package com.gestion_employe.app.services;

import com.gestion_employe.app.dtos.EmployeDTO;
import com.gestion_employe.app.entities.Departement;
import com.gestion_employe.app.entities.Employe;
import com.gestion_employe.app.entities.Role;
import com.gestion_employe.app.entities.User;
import com.gestion_employe.app.repositories.DepartementRepository;
import com.gestion_employe.app.repositories.EmployeRepository;
import com.gestion_employe.app.repositories.RoleRepository;
import com.gestion_employe.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private DepartementRepository departementRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Employe toEntity(EmployeDTO dto) {
        Departement departement = null;
        if (dto.getDepartementId() != null) {
            departement = departementRepository.findById(dto.getDepartementId()).orElse(null);
        }

        Employe employeChef = null;
        if (dto.getEmployeChefId() != null) {
            employeChef = employeRepository.findById(dto.getEmployeChefId()).orElse(null);
        }

        Employe employe = new Employe(dto.getId(), dto.getNom(), dto.getPrenom(),
                dto.getNoMatricule(), dto.getTel(), departement);
        employe.setEmployeChef(employeChef);

        return employe;
    }

    private EmployeDTO toDTO(Employe employe) {
        return new EmployeDTO(
                employe.getId(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getNoMatricule(),
                employe.getTel(),
                employe.getDepartement() != null ? employe.getDepartement().getId() : null,
                employe.getEmployeChef() != null ? employe.getEmployeChef().getId() : null,
                null
        );
    }

    // NOUVEAU: Ajout de la méthode getAllEmployes qui renvoie une List
    public List<EmployeDTO> getAllEmployes() {
        return employeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // NOUVEAU: Ajout de la méthode getAllEmployes qui renvoie une Page
    public Page<EmployeDTO> getAllEmployes(Pageable pageable) {
        return employeRepository.findAll(pageable).map(this::toDTO);
    }

    public EmployeDTO getEmployeById(Long id) {
        return employeRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public EmployeDTO createEmploye(EmployeDTO dto) {
        // 1. Vérifier si un employé avec ce matricule existe déjà
        if (employeRepository.findByNoMatricule(dto.getNoMatricule()) != null) {
            throw new RuntimeException("Un employé avec ce matricule existe déjà !");
        }

        Departement departement = departementRepository.findById(dto.getDepartementId())
                .orElseThrow(() -> new RuntimeException("Département introuvable"));

        long nbEmp = employeRepository.countByDepartementId(departement.getId());
        if (nbEmp >= departement.getTaille()) {
            throw new RuntimeException("Département complet !");
        }

        Employe savedEmploye = employeRepository.save(toEntity(dto));

        User newUser = new User();
        newUser.setUsername(dto.getNoMatricule());
        String defaultPassword = "password123";
        newUser.setPassword(passwordEncoder.encode(defaultPassword));

        List<Role> roles = new ArrayList<>();
        if (dto.getRoles() != null) {
            for (String roleName : dto.getRoles()) {
                Role role = roleRepository.findByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        newUser.setRoles(roles);

        newUser.setEmploye(savedEmploye);

        userRepository.save(newUser);

        return toDTO(savedEmploye);
    }

    public EmployeDTO updateEmploye(Long id, EmployeDTO dto) {
        return employeRepository.findById(id).map(existing -> {
            existing.setNom(dto.getNom());
            existing.setPrenom(dto.getPrenom());
            existing.setNoMatricule(dto.getNoMatricule());
            existing.setTel(dto.getTel());
            if (dto.getDepartementId() != null) {
                Departement departement = departementRepository.findById(dto.getDepartementId()).orElse(null);
                existing.setDepartement(departement);
            }
            if (dto.getEmployeChefId() != null) {
                Employe employeChef = employeRepository.findById(dto.getEmployeChefId()).orElse(null);
                existing.setEmployeChef(employeChef);
            }

            User existingUser = userRepository.findByEmployeId(existing.getId());
            if (existingUser != null && dto.getRoles() != null) {
                List<Role> roles = new ArrayList<>();
                for (String roleName : dto.getRoles()) {
                    Role role = roleRepository.findByName(roleName);
                    if (role != null) {
                        roles.add(role);
                    }
                }
                existingUser.setRoles(roles);
                userRepository.save(existingUser);
            }

            return toDTO(employeRepository.save(existing));
        }).orElse(null);
    }

    public boolean deleteEmploye(Long id) {
        if (!employeRepository.existsById(id)) return false;

        User user = userRepository.findByEmployeId(id);
        if(user != null) {
            userRepository.delete(user);
        }

        employeRepository.deleteById(id);
        return true;
    }
}