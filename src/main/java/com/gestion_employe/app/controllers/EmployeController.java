package com.gestion_employe.app.controllers;

import com.gestion_employe.app.dtos.EmployeDTO;
import com.gestion_employe.app.services.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employes")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Modifié ici
    public ResponseEntity<List<EmployeDTO>> getAllEmployes() {
        List<EmployeDTO> employes = employeService.getAllEmployes();
        return ResponseEntity.ok(employes);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMIN')") // Modifié ici
    public ResponseEntity<Page<EmployeDTO>> getAllEmployes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeDTO> employes = employeService.getAllEmployes(pageable);
        return ResponseEntity.ok(employes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE_CHEF', 'EMPLOYE')") // Modifié ici
    public ResponseEntity<EmployeDTO> getEmployeById(@PathVariable Long id) {
        EmployeDTO employe = employeService.getEmployeById(id);
        if (employe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employe);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')") // Modifié ici
    public ResponseEntity<EmployeDTO> createEmploye(@RequestBody EmployeDTO dto) {
        EmployeDTO created = employeService.createEmploye(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Modifié ici
    public ResponseEntity<EmployeDTO> updateEmploye(@PathVariable Long id, @RequestBody EmployeDTO dto) {
        EmployeDTO updated = employeService.updateEmploye(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Modifié ici
    public ResponseEntity<Void> deleteEmploye(@PathVariable Long id) {
        if (!employeService.deleteEmploye(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}