package com.gestion_employe.app.controllers;

import com.gestion_employe.app.dtos.AuthResponseDTO;
import com.gestion_employe.app.dtos.LoginRequestDTO;
import com.gestion_employe.app.entities.User;
import com.gestion_employe.app.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.authenticateUser(loginRequest);
        if (response==null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = authService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
}
