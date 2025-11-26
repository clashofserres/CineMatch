package com.clashofserres.cinematch.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TmdbMovieDTO(
        Long id,
        String title,
        String overview,

        @JsonProperty("release_date")
        String releaseDate,

        @JsonProperty("poster_path")
        String posterPath,

        Double popularity,

        @JsonProperty("vote_average")
        Double voteAverage,

        @JsonProperty("vote_count")
        Integer voteCount,

        @JsonProperty("genre_ids")
        List<Integer> genreIds
) {}