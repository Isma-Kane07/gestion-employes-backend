package com.gestion_employe.app.controllers;

import com.gestion_employe.app.dtos.DepartementDTO;
import com.gestion_employe.app.dtos.MissionDTO;
import com.gestion_employe.app.services.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/missions")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Correction
    public ResponseEntity<List<MissionDTO>> getAllMissions() {
        List<MissionDTO> missions = missionService.getAllMissions();
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ADMIN')") // Correction
    public ResponseEntity<Page<MissionDTO>> getAllMissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MissionDTO> missions = missionService.getAllMissions(pageable);
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Correction
    public ResponseEntity<MissionDTO> getMissionById(@PathVariable Long id) {
        MissionDTO mission = missionService.getMissionById(id);
        if (mission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mission);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')") // Correction
    public ResponseEntity<MissionDTO> createMission(@RequestBody MissionDTO dto) {
        MissionDTO created = missionService.createMission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Correction
    public ResponseEntity<MissionDTO> updateMission(@PathVariable Long id, @RequestBody MissionDTO dto) {
        MissionDTO updated = missionService.updateMission(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Correction
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        if (!missionService.deleteMission(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-employe/{employeId}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('EMPLOYE') and authentication.principal.username == @employeRepository.findById(#employeId).orElse(null)?.user?.username) or (hasAuthority('EMPLOYE_CHEF') and @employeRepository.findById(#employeId).orElse(null)?.employeChef?.user?.username == authentication.principal.username)")
    public ResponseEntity<List<MissionDTO>> getMissionsByEmploye(@PathVariable Long employeId) {
        List<MissionDTO> missions = missionService.getMissionsByEmploye(employeId);
        return ResponseEntity.ok(missions);
    }
}