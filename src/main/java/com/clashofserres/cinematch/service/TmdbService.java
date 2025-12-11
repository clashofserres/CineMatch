package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.config.TmdbConfig;
import com.clashofserres.cinematch.data.dto.*; // Import  DTOs
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.clashofserres.cinematch.data.dto.TmdbPersonProfileDTO;
import com.clashofserres.cinematch.data.dto.TmdbCombinedCreditsResponseDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import java.util.Comparator;

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

        return enrichMovieWithKPIs(body);
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
    // ----------------------------------
    // KPI CALCULATION METHODS
    // ----------------------------------


    private TmdbMovieDTO enrichMovieWithKPIs(TmdbMovieDTO movie) {

        Double starPower = calculateStarPowerIndex(movie.credits());


        Double boxOffice = calculateBoxOfficeScore(movie.voteCount(), movie.revenue());


        return new TmdbMovieDTO(
                movie.id(),
                movie.title(),
                movie.overview(),
                movie.releaseDate(),
                movie.posterPath(),
                movie.backdropPath(),
                movie.popularity(),
                movie.voteAverage(),
                movie.voteCount(),
                movie.genreIds(),
                movie.runtime(),
                movie.revenue(),
                movie.genres(),
                movie.credits(),
                starPower,
                boxOffice
        );
    }

    private Double calculateStarPowerIndex(TmdbCreditsDTO credits) {

        if (credits == null || credits.cast() == null || credits.cast().isEmpty()) {
            return 0.0;
        }

        return credits.cast().stream()

                .sorted(Comparator.comparing(TmdbCastMemberDTO::popularity, Comparator.nullsLast(Comparator.reverseOrder())))

                .limit(5)

                .mapToDouble(actor -> actor.popularity() != null ? actor.popularity() : 0.0)
                .sum();
    }

    private Double calculateBoxOfficeScore(Integer voteCount, Long revenue) {
        double safeVoteCount = (voteCount != null) ? voteCount : 0.0;
        double safeRevenue = (revenue != null) ? revenue : 0.0;


        return (safeRevenue / 1_000_000) + (safeVoteCount / 100);
    }
    public TmdbPersonProfileDTO getPersonDetails(long id) {


        String bioUrl = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/person/" + id)
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbPersonProfileDTO> bioResponse = restTemplate.exchange(
                bioUrl, HttpMethod.GET, buildHeaders(), TmdbPersonProfileDTO.class
        );

        TmdbPersonProfileDTO personProfile = bioResponse.getBody();

        if (personProfile == null) {
            throw new TmdbServiceException("Person profile not found for ID: " + id);
        }


        String creditsUrl = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/person/" + id + "/combined_credits")
                .queryParam("api_key", tmdbConfig.getKey())
                .toUriString();

        ResponseEntity<TmdbCombinedCreditsResponseDTO> creditsResponse = restTemplate.exchange(
                creditsUrl, HttpMethod.GET, buildHeaders(), TmdbCombinedCreditsResponseDTO.class
        );

        TmdbCombinedCreditsResponseDTO creditsBody = creditsResponse.getBody();



        List<TmdbMovieDTO> filmography = new ArrayList<>();

        if (creditsBody != null) {
            if (creditsBody.cast() != null) {
                filmography.addAll(creditsBody.cast());
            }
            if (creditsBody.crew() != null) {
                filmography.addAll(creditsBody.crew());
            }
        }


        return new TmdbPersonProfileDTO(
                personProfile.id(),
                personProfile.name(),
                personProfile.profilePath(),
                personProfile.biography(),
                personProfile.birthday(),
                personProfile.knownForDepartment(),
                filmography
        );
    }

    public TmdbMovieListResponseDTO getMoviesByGenres(List<Integer> genreIds) {

        String genresParam = genreIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));

        String url = UriComponentsBuilder
                .fromUriString(tmdbConfig.getBaseUrl() + "/discover/movie")
                .queryParam("api_key", tmdbConfig.getKey())
                .queryParam("with_genres", genresParam) // Φίλτρο ειδών
                .queryParam("sort_by", "popularity.desc") // Φέρε τις δημοφιλείς αυτών των ειδών
                .queryParam("language", "en-US")
                .toUriString();

        ResponseEntity<TmdbMovieListResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.GET, buildHeaders(), TmdbMovieListResponseDTO.class);

        TmdbMovieListResponseDTO body = response.getBody();
        return body != null ? body : new TmdbMovieListResponseDTO(0, java.util.List.of(), 0, 0);
    }
}
