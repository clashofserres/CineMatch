package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.config.TmdbConfig;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
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

    public TmdbMovieListResponseDTO searchMovies(String query) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/search/movie")
                .queryParam("query", query)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbMovieListResponseDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), TmdbMovieListResponseDTO.class);

        TmdbMovieListResponseDTO body = response.getBody();

        return body != null ? body : new TmdbMovieListResponseDTO(0, java.util.List.of(), 0, 0);
    }

    public TmdbMovieDTO getMovieDetails(long id) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/movie/" + id)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbMovieDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), TmdbMovieDTO.class);

        TmdbMovieDTO body = response.getBody();

        if (body == null) {
            throw new TmdbServiceException("No movie found with id " + id);
        }

        return body;
    }

    // ----------------------------------
    // POPULAR MOVIES
    // ----------------------------------
    public TmdbMovieListResponseDTO getPopularMovies() {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/movie/popular")
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbMovieListResponseDTO> response =
                restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), TmdbMovieListResponseDTO.class);

        TmdbMovieListResponseDTO body = response.getBody();

        return body != null ? body : new TmdbMovieListResponseDTO(0, java.util.List.of(), 0, 0);
    }
}
