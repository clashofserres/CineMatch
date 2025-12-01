package com.clashofserres.cinematch.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbPersonDTO(
        Long id,
        String name,
        String knownForDepartment,
        @JsonProperty("profile_path") String profilePath //
) {

}
