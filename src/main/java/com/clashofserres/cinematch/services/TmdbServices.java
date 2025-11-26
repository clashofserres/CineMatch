package com.clashofserres.cinematch.services;

import com.clashofserres.cinematch.config.TmdbConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbServices {

    private final TmdbConfig tmdbConfig;
    private final RestTemplate restTemplate;

    public TmdbServices(TmdbConfig tmdbConfig) {
        this.tmdbConfig = tmdbConfig;
        this.restTemplate = new RestTemplate();
    }

    private HttpEntity<String> buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tmdbConfig.getToken());
        headers.set("Accept", "application/json");
        return new HttpEntity<>(headers);
    }

    public String searchMovies(String query) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/search/movie")
                .queryParam("query", query)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), String.class);
        return res.getBody() != null ? res.getBody() : "{}";
    }

    public String getMovieDetails(long id) {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/movie/" + id)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), String.class);
        return res.getBody() != null ? res.getBody() : "{}";
    }

    public String getPopularMovies() {
        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/movie/popular")
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), String.class);
        return res.getBody() != null ? res.getBody() : "{}";
    }
}
