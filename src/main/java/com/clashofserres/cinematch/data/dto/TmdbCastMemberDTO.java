package com.clashofserres.cinematch.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbCastMemberDTO(
        Long id,
        String name,
        String character,
        @JsonProperty("profile_path") String profilePath,
        Integer order) {
}
