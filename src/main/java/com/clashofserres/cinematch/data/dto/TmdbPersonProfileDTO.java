package com.clashofserres.cinematch.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record TmdbPersonProfileDTO(
        Long id,
        String name,


        @JsonProperty("profile_path")
        String profilePath,
        String biography,
        @JsonProperty("birthday")
        LocalDate birthday,
        @JsonProperty("known_for_department")
        String knownForDepartment,


        List<TmdbMovieDTO> filmography
) {}