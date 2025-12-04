package com.clashofserres.cinematch.controller;

import com.clashofserres.cinematch.data.dto.TmdbPersonProfileDTO;
import com.clashofserres.cinematch.service.TmdbService;
import com.clashofserres.cinematch.service.TmdbService.TmdbServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final TmdbService tmdbService;


    public PersonController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmdbPersonProfileDTO> getPersonProfile(@PathVariable Long id) {
        try {

            TmdbPersonProfileDTO details = tmdbService.getPersonDetails(id);

            if (details == null) {
                return ResponseEntity.notFound().build();
            }


            return ResponseEntity.ok(details);

        } catch (TmdbServiceException e) {

            return ResponseEntity.status(404).build();
        }
    }
}