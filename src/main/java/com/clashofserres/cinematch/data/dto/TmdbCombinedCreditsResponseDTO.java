package com.clashofserres.cinematch.data.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;


public record TmdbCombinedCreditsResponseDTO(
        @JsonProperty("cast")
        List<TmdbMovieDTO> cast,

        @JsonProperty("crew")
        List<TmdbMovieDTO> crew
) {}