package com.clashofserres.cinematch.controllers;

import com.clashofserres.cinematch.services.TmdbServices;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final TmdbServices tmdbService;

    public TmdbController(TmdbServices tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam String query) {
        return tmdbService.searchMovies(query);
    }

    @GetMapping("/movie/{id}")
    public String getMovieDetails(@PathVariable long id) {
        return tmdbService.getMovieDetails(id);
    }

    @GetMapping("/popular")
    public String getPopularMovies() {
        return tmdbService.getPopularMovies();
    }
}
