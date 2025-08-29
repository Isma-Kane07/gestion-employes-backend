package com.gestion_employe.app.controllers;

import com.gestion_employe.app.entities.Parametre;
import com.gestion_employe.app.exceptions.AppNotFoundException;
import com.gestion_employe.app.exceptions.InternalAppException;
import com.gestion_employe.app.services.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parametres")
public class ParametreController {
    @Autowired
    private ParametreService parametreService;

    @GetMapping("/find")
    public Parametre findByKey(@RequestParam String key) throws AppNotFoundException, InternalAppException {
        return parametreService.findByKey(key);
    }
}
