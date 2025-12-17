package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
import com.clashofserres.cinematch.data.model.MovieEntity;
import com.clashofserres.cinematch.data.model.UserEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final TmdbService tmdbService;

    public RecommendationService(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    public List<TmdbMovieDTO> getRecommendationsForUser(UserEntity user) {
        Set<MovieEntity> watchlist = user.getWatchList();


        if (watchlist == null || watchlist.isEmpty()) {
            return tmdbService.getPopularMovies().results();
        }


        List<Integer> topGenreIds = getTopGenresFromWatchlist(watchlist);


        TmdbMovieListResponseDTO response = tmdbService.getMoviesByGenres(topGenreIds);
        List<TmdbMovieDTO> recommendations = new ArrayList<>(response.results());


        MovieEntity randomFav = watchlist.stream().findAny().orElse(null);
        if (randomFav != null) {
            TmdbMovieListResponseDTO similar = tmdbService.getSimilarMovies(randomFav.getId());
            recommendations.addAll(similar.results());
        }


        Set<Long> watchedMovieIds = watchlist.stream()
                .map(MovieEntity::getId)
                .collect(Collectors.toSet());

        return recommendations.stream()
                .filter(movie -> !watchedMovieIds.contains(movie.id())) // Όχι αυτές που είδαμε
                .distinct() // Όχι διπλότυπα
                .limit(12) // Κράτα μόνο 12 για το UI
                .collect(Collectors.toList());
    }

    private List<Integer> getTopGenresFromWatchlist(Set<MovieEntity> watchlist) {

        Map<Integer, Long> genreFrequency = watchlist.stream()
                .flatMap(movie -> movie.getGenreIds().stream()) // Stream<Long>
                .map(Long::intValue) // Μετατροπή σε Integer
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        return genreFrequency.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    }
