package com.clashofserres.cinematch.data.dto;

import java.util.List;

public class TmdbPersonListResponseDTO {
    private int page;
    private List<TmdbPersonDTO> results;
    private int totalResults;
    private int totalPages;

    // Constructors, getters, and setters
    public TmdbPersonListResponseDTO(int page, List<TmdbPersonDTO> results, int totalResults, int totalPages) {
        this.page = page;
        this.results = results;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public List<TmdbPersonDTO> results() {
        return results;
    }
}
