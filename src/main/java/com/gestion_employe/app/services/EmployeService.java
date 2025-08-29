package com.gestion_employe.app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_employe.app.dtos.EmployeDTO;
import com.gestion_employe.app.dtos.GradeDTO;
import com.gestion_employe.app.entities.*;
import com.gestion_employe.app.exceptions.AppNotFoundException;
import com.gestion_employe.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Assurez-vous d'importer ceci

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
    @Autowired
    private ParametreRepository parametreRepository;

    /**
     * Convertit un DTO d'employé en entité Employe.
     * @param dto Le DTO de l'employé.
     * @return L'entité Employe.
     */
    private Employe toEntity(EmployeDTO dto) {
        Departement departement = null;
        if (dto.getDepartementId() != null) {
            departement = departementRepository.findById(dto.getDepartementId()).orElse(null);
        }

        Employe employeChef = null;
        if (dto.getEmployeChefId() != null) {
            employeChef = employeRepository.findById(dto.getEmployeChefId()).orElse(null);
        }

        Employe employe =  Employe.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .tel(dto.getTel())
                .noMatricule(dto.getNoMatricule())
                .departement(departement)
                .build();

        employe.setEmployeChef(employeChef);

        return employe;
    }

    /**
     * Convertit une entité Employe en DTO d'employé, en incluant les rôles de l'utilisateur associé.
     * Cette méthode est @Transactional(readOnly = true) pour s'assurer que les rôles sont chargés correctement
     * si la relation est LAZY ou si l'entité User n'est pas déjà dans le contexte de persistance.
     *
     * @param employe L'entité Employe.
     * @return Le DTO de l'employé.
     */
    @Transactional(readOnly = true) // Important pour s'assurer que les rôles sont chargés dans cette transaction
    private EmployeDTO toDTO(Employe employe) {
        List<String> roles = new ArrayList<>();
        // Chercher l'utilisateur associé à cet employé par son ID d'employé
        User user = userRepository.findByEmployeId(employe.getId());
        if (user != null && user.getRoles() != null) {
            roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
        }

        // Dédue la propriété 'isChef' de la liste des rôles
        boolean isChef = roles.contains("EMPLOYE_CHEF");

        return new EmployeDTO(
                employe.getId(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getNoMatricule(),
                employe.getTel(),
                employe.getDepartement() != null ? employe.getDepartement().getId() : null,
                employe.getEmployeChef() != null ? employe.getEmployeChef().getId() : null,
                roles, // CORRIGÉ: Passer la liste de rôles réelle ici
                isChef // NOUVEAU: Passer le statut de chef
        );
    }

    @Transactional(readOnly = true) // Assurez-vous que les rôles sont chargés lors de la récupération de tous les employés
    public List<EmployeDTO> getAllEmployes() {
        return employeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EmployeDTO> getAllEmployes(Pageable pageable) {
        return employeRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public EmployeDTO getEmployeById(Long id) {
        return employeRepository.findById(id).map(this::toDTO).orElse(null);
    }

    /**
     * Crée un nouvel employé et un utilisateur associé.
     * Attribue toujours le rôle EMPLOYE par défaut et EMPLOYE_CHEF si spécifié dans le DTO via `isChef`.
     * @param dto Les données du nouvel employé.
     * @return Le DTO de l'employé créé.
     * @throws RuntimeException si le matricule existe déjà, le département est introuvable, complet,
     * ou si un rôle requis n'existe pas en base de données.
     */
    @Transactional // Rendre cette méthode transactionnelle pour toutes les opérations DB
    public EmployeDTO createEmploye(EmployeDTO dto) {
        if (dto.getNoMatricule() != null && employeRepository.findByNoMatricule(dto.getNoMatricule()) != null) {
            throw new RuntimeException("Un employé avec ce matricule existe déjà !");
        }

        Departement departement = departementRepository.findById(dto.getDepartementId())
                .orElseThrow(() -> new RuntimeException("Département introuvable"));

        long nbEmp = employeRepository.countByDepartementId(departement.getId());
        if (nbEmp >= departement.getTaille()) {
            throw new RuntimeException("Département complet !");
        }

        Employe employeToSave = toEntity(dto);
        Employe savedEmploye = employeRepository.save(employeToSave);

        User newUser = new User();
        newUser.setUsername(dto.getNoMatricule());
        String defaultPassword = "password123"; // Avertissement de sécurité: à améliorer en production
        newUser.setPassword(passwordEncoder.encode(defaultPassword));

        List<Role> roles = new ArrayList<>();
        // Toujours attribuer le rôle EMPLOYE par défaut à tout nouvel employé
        Role employeRoleEntity = roleRepository.findByName("EMPLOYE");
        if (employeRoleEntity == null) {
            throw new RuntimeException("Le rôle 'EMPLOYE' n'existe pas dans la base de données. Veuillez le créer.");
        }
        roles.add(employeRoleEntity);

        // Ajouter le rôle EMPLOYE_CHEF si `isChef` est vrai dans le DTO
        if (dto.isChef()) {
            Role chefRoleEntity = roleRepository.findByName("EMPLOYE_CHEF");
            if (chefRoleEntity == null) {
                throw new RuntimeException("Le rôle 'EMPLOYE_CHEF' n'existe pas dans la base de données. Veuillez le créer.");
            }
            roles.add(chefRoleEntity);
        }
        newUser.setRoles(roles);
        newUser.setEmploye(savedEmploye);
        userRepository.save(newUser);

        return toDTO(savedEmploye);
    }

    /**
     * Met à jour un employé existant.
     * Gère la mise à jour des rôles de l'utilisateur associé en fonction du flag `isChef` du DTO.
     * @param id L'ID de l'employé à mettre à jour.
     * @param dto Les nouvelles données de l'employé.
     * @return Le DTO de l'employé mis à jour, ou null si l'ID n'est pas trouvé.
     * @throws RuntimeException si un rôle requis ('EMPLOYE' ou 'EMPLOYE_CHEF') n'existe pas en DB.
     */
    @Transactional // Rendre cette méthode transactionnelle pour toutes les opérations DB
    public EmployeDTO updateEmploye(Long id, EmployeDTO dto) {
        return employeRepository.findById(id).map(existing -> {
            existing.setNom(dto.getNom());
            existing.setPrenom(dto.getPrenom());
            existing.setNoMatricule(dto.getNoMatricule());
            existing.setTel(dto.getTel());

            // Mise à jour du département
            if (dto.getDepartementId() != null) {
                Departement departement = departementRepository.findById(dto.getDepartementId()).orElse(null);
                existing.setDepartement(departement);
            } else {
                existing.setDepartement(null); // Si l'ID est null, supprime le département
            }

            // Mise à jour du chef d'équipe
            if (dto.getEmployeChefId() != null) {
                Employe employeChef = employeRepository.findById(dto.getEmployeChefId()).orElse(null);
                existing.setEmployeChef(employeChef);
            } else {
                existing.setEmployeChef(null); // Si l'ID est null, supprime le chef
            }

            // Mise à jour des rôles de l'utilisateur associé
            User existingUser = userRepository.findByEmployeId(existing.getId());
            if (existingUser != null) { // Seulement si un utilisateur est associé à l'employé
                List<Role> updatedRoles = new ArrayList<>();

                Role employeRoleEntity = roleRepository.findByName("EMPLOYE");
                if (employeRoleEntity == null) {
                    throw new RuntimeException("Le rôle 'EMPLOYE' n'existe pas dans la base de données. Veuillez le créer.");
                }
                updatedRoles.add(employeRoleEntity); // Toujours inclure le rôle EMPLOYE

                if (dto.isChef()) { // Si le DTO indique que c'est un chef
                    Role chefRoleEntity = roleRepository.findByName("EMPLOYE_CHEF");
                    if (chefRoleEntity == null) {
                        throw new RuntimeException("Le rôle 'EMPLOYE_CHEF' n'existe pas dans la base de données. Veuillez le créer.");
                    }
                    if (!updatedRoles.contains(chefRoleEntity)) { // Éviter les doublons
                        updatedRoles.add(chefRoleEntity);
                    }
                }

                existingUser.setRoles(updatedRoles); // Définit les nouveaux rôles
                userRepository.save(existingUser);
            }

            return toDTO(employeRepository.save(existing));
        }).orElse(null);
    }

    /**
     * Supprime un employé et l'utilisateur associé.
     * @param id L'ID de l'employé à supprimer.
     * @return Vrai si la suppression a réussi, faux sinon.
     */
    @Transactional
    public boolean deleteEmploye(Long id) {
        if (!employeRepository.existsById(id)) return false;

        User user = userRepository.findByEmployeId(id);
        if(user != null) {
            userRepository.delete(user);
        }

        employeRepository.deleteById(id);
        return true;
    }

    public List<EmployeDTO> findManagers(Long departmentId) throws AppNotFoundException, JsonProcessingException {
        var parameter = parametreRepository.findByKey(Parametre.EMPLOYEE_GRADES).orElseThrow(AppNotFoundException::new);

        List<GradeDTO> values = new ObjectMapper().readValue(parameter.getValue(), new TypeReference<List<GradeDTO>>() {});
        var employees = employeRepository.findByDepartmentIdAndGrade(departmentId, values.stream().map(GradeDTO::libele).toList());
   return  employees.stream().map(this::toDTO).toList();
    }
}