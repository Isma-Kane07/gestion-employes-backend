package com.gestion_employe.app.controllers;

import com.gestion_employe.app.dtos.DepartementDTO;
import com.gestion_employe.app.dtos.MissionDTO;
import com.gestion_employe.app.services.MissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "/*")
@RestController
@RequestMapping("/missions")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @GetMapping
    public ResponseEntity<List<MissionDTO>> getAllMissions() {
        List<MissionDTO> missions = missionService.getAllMissions();
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<MissionDTO>> getAllMissionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<MissionDTO> result = missionService.getAllMissions(PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }


    @PostMapping("/create")
    public ResponseEntity<MissionDTO> createMission(@RequestBody MissionDTO dto) {
        MissionDTO created = missionService.createMission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<MissionDTO>> getMissionsByEmploye(@PathVariable Long employeId) {
        List<MissionDTO> missions = missionService.getMissionsByEmploye(employeId);
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionDTO> getMissionById(@PathVariable Long id) {
        MissionDTO mission = missionService.getMissionById(id);
        if (mission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mission);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MissionDTO> updateMission(@PathVariable Long id, @RequestBody MissionDTO dto) {
        MissionDTO updated = missionService.updateMission(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        boolean deleted = missionService.deleteMission(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}