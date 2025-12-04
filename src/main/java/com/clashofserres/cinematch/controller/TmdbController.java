package com.clashofserres.cinematch.controller;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
import com.clashofserres.cinematch.data.dto.TmdbPersonListResponseDTO;
import com.clashofserres.cinematch.service.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/search")
    public TmdbMovieListResponseDTO searchMovies(@RequestParam String query) {
        return tmdbService.searchMovies(query);
    }

    @GetMapping("/movie/{id}")
    public TmdbMovieDTO getMovieDetails(@PathVariable long id) {
        return tmdbService.getMovieDetails(id);
    }

    @GetMapping("/person/search")
    public ResponseEntity<TmdbPersonListResponseDTO> searchPeople(@RequestParam String query) {
        return ResponseEntity.ok(tmdbService.searchPeople(query));
    }

    @GetMapping("/person/popular")
    public ResponseEntity<TmdbPersonListResponseDTO> getPopularPeople() {
        return ResponseEntity.ok(tmdbService.getPopularPeople());
    }


}
