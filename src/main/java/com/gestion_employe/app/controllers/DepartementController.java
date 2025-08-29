package com.gestion_employe.app.controllers;

import com.gestion_employe.app.dtos.DepartementDTO;
import com.gestion_employe.app.services.DepartementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/departements")
public class DepartementController {

    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<DepartementDTO> getAllDepartements() {
        return departementService.getAllDepartements();
    }

    @GetMapping("/taille-actuelle/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Integer> getTailleActuelle(@PathVariable Long id) {
        int taille = departementService.getTailleActuelle(id);
        return ResponseEntity.ok(taille);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<DepartementDTO>> getAllDepartementsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<DepartementDTO> result = departementService.getAllDepartements(PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYE_CHEF', 'EMPLOYE')")
    public ResponseEntity<DepartementDTO> getDepartementByID(@PathVariable Long id) {
        DepartementDTO departement = departementService.getDepartementById(id);
        if (departement == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(departement);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DepartementDTO> createDepartement(@RequestBody DepartementDTO dto) {
        DepartementDTO created = departementService.createDepartement(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DepartementDTO> updateDepartement(@PathVariable Long id, @RequestBody DepartementDTO dto) {
        DepartementDTO updated = departementService.updateDepartement(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        boolean deleted = departementService.deleteDepartement(id);
        if(!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}