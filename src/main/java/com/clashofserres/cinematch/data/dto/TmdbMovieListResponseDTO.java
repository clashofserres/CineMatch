package com.clashofserres.cinematch.data.dto;

import java.util.List;

public record TmdbMovieListResponseDTO(
        int page,
        List<TmdbMovieDTO> results,
        int total_pages,
        int total_results
) {}
