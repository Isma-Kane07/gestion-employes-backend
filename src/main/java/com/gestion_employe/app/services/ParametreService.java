package com.gestion_employe.app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_employe.app.entities.Parametre;
import com.gestion_employe.app.exceptions.AppNotFoundException;
import com.gestion_employe.app.exceptions.InternalAppException;
import com.gestion_employe.app.repositories.ParametreRepository;
import org.springframework.stereotype.Service;

@Service
public class ParametreService {
    ParametreRepository parametreRepository;

    ParametreService(ParametreRepository parametreRepository){
        this.parametreRepository = parametreRepository;
    }

    public Parametre findByKey (String key) throws AppNotFoundException {
       return parametreRepository.findByKey(key).orElseThrow(AppNotFoundException::new);
    }
}
