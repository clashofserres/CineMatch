package com.clashofserres.cinematch.data.dto;

import java.util.List;

public record TmdbCreditsDTO(
        List<TmdbCastMemberDTO> cast,
        List<TmdbCastMemberDTO> crew) {
}