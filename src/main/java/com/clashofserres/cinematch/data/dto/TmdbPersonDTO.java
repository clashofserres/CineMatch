package com.clashofserres.cinematch.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbPersonDTO(
        Long id,
        String name,

        @JsonProperty("profile_path")
        String profilePath,

        @JsonProperty("known_for_department")
        String knownForDepartment,

        Double popularity
) {}