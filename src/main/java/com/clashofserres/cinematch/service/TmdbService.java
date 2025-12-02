package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.config.TmdbConfig;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
import com.clashofserres.cinematch.data.dto.TmdbPersonDTO;
import com.clashofserres.cinematch.data.dto.TmdbPersonListResponseDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbService {

    public static class TmdbServiceException extends RuntimeException {
        public TmdbServiceException(String message) {
            super(message);
        }
    }

    private final TmdbConfig tmdbConfig;
    private final RestTemplate restTemplate;

    public TmdbService(TmdbConfig tmdbConfig) {
        this.tmdbConfig = tmdbConfig;
        this.restTemplate = new RestTemplate();
    }

    private HttpEntity<String> buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tmdbConfig.getToken());
        headers.set("Accept", "application/json");
        return new HttpEntity<>(headers);
    }

    // ----------------------------------
    // MOVIE METHODS
    // ----------------------------------

    public TmdbMovieListResponseDTO searchMovies(String query) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/search/movie")
                .queryParam("query", query)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbMovieListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(),
                TmdbMovieListResponseDTO.class);

        TmdbMovieListResponseDTO body = response.getBody();
        return body != null ? body : new TmdbMovieListResponseDTO(0, java.util.List.of(), 0, 0);
    }

    public TmdbMovieDTO getMovieDetails(long id) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/movie/" + id)
                .queryParam("api_key", tmdbConfig.getKey())
                .queryParam("append_to_response", "credits")
                .toUriString();

        ResponseEntity<TmdbMovieDTO> response = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(),
                TmdbMovieDTO.class);

        TmdbMovieDTO body = response.getBody();

        if (body == null) {
            throw new TmdbServiceException("No movie found with id " + id);
        }

        return body;
    }

    public TmdbMovieListResponseDTO getPopularMovies() {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/movie/popular")
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbMovieListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(),
                TmdbMovieListResponseDTO.class);

        TmdbMovieListResponseDTO body = response.getBody();
        return body != null ? body : new TmdbMovieListResponseDTO(0, java.util.List.of(), 0, 0);
    }

    // ----------------------------------
    // ACTOR (PERSON) METHODS
    // ----------------------------------


    public TmdbPersonListResponseDTO searchPeople(String query) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/search/person")
                .queryParam("query", query)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbPersonListResponseDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), TmdbPersonListResponseDTO.class);

        TmdbPersonListResponseDTO body = response.getBody();


        if (body != null) {
            body.results().forEach(person ->
                    System.out.println("Actor: " + person.name() + ", Profile Path: " + person.profilePath())
            );
        }

        return body != null ? body : new TmdbPersonListResponseDTO(0, java.util.List.of(), 0, 0);
    }



    // Method to fetch popular actors
    public TmdbPersonListResponseDTO getPopularPeople() {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/person/popular")
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbPersonListResponseDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), TmdbPersonListResponseDTO.class);

        TmdbPersonListResponseDTO body = response.getBody();
        return body != null ? body : new TmdbPersonListResponseDTO(0, java.util.List.of(), 0, 0);
    }
}
