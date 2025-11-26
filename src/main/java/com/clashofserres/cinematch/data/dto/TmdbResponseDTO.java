package com.clashofserres.cinematch.data.dto;

import java.util.List;

public record TmdbResponseDTO(

        List<TmdbMovieDTO> results
) {}