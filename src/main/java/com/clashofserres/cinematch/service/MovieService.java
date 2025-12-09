package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.dto.TmdbCastMemberDTO;
import com.clashofserres.cinematch.data.dto.TmdbGenreDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.model.ActorEntity;
import com.clashofserres.cinematch.data.model.MovieEntity;
import com.clashofserres.cinematch.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ActorService actorService;

    public MovieService(MovieRepository movieRepository, ActorService actorService) {
        this.movieRepository = movieRepository;
        this.actorService = actorService;
    }

    public Optional<MovieEntity> findMovieByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    public Optional<MovieEntity> findMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Optional<MovieEntity> addOrGetmovieEntityFromDTO(TmdbMovieDTO movieDTO) {
        //try {
            Optional<MovieEntity> actor = findMovieById(movieDTO.id());
            if (actor.isPresent()) {
                return actor;
            }
            else {
                MovieEntity newMovie = movieEntityFromDTO(movieDTO);
                movieRepository.save(newMovie);

                return Optional.of(newMovie);
            }
       // }
        //catch (Exception e) {
         //   return Optional.empty();
       // }
    }

    private MovieEntity movieEntityFromDTO(TmdbMovieDTO movieDTO) {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(movieDTO.id());
        movieEntity.setTitle(movieDTO.title());
        movieEntity.setPosterPath(movieDTO.posterPath());
        movieEntity.setOverview(movieDTO.overview());
        movieEntity.setReleaseDate(parseMovieDTODate(movieDTO.releaseDate()));

        //movieRepository.save(movieEntity); // Pre save so we can insert genres

        movieEntity.setGenreIds(getGenreIds(movieDTO.genres()));
        movieEntity.setCast(getCastFromMovieDTO(movieDTO.credits().cast()));

        return movieEntity;
    }

    private Set<ActorEntity> getCastFromMovieDTO(List<TmdbCastMemberDTO> castDTOList) {
        return actorService.addOrGetActorsFromCastDTOList(castDTOList);
    }

    private Set<Long> getGenreIds(List<TmdbGenreDTO> genreDTOs) {
        Set<Long> genreIds = new HashSet<>();
        for (TmdbGenreDTO genreDTO : genreDTOs) {
            genreIds.add(genreDTO.id());
        }
        return genreIds;
    }

    private LocalDate parseMovieDTODate(String date) {
        try
        {
            return LocalDate.parse(date);
        }
        catch (Exception e) {
            return LocalDate.now();
        }
    }
}
