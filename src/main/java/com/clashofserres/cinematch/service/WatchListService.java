package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.model.MovieEntity;
import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.repository.MovieRepository;
import com.clashofserres.cinematch.repository.UserRepository;
import org.apache.catalina.User;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WatchListService {

    private final MovieRepository movieRepository;

    public class WatchListServiceFail extends Exception
    {
        public WatchListServiceFail(String message)
        {
            super(message);
        }
    }

    private final UserRepository userRepository;
    private final UserService userService;
    private final MovieService movieService;

    public WatchListService(UserRepository userRepository,
                            UserService userService,
                            MovieService movieService, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.movieService = movieService;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public void addToWatchList(TmdbMovieDTO movieDTO) throws WatchListServiceFail {
        try
        {
            UserEntity myUser = userService.getMyUserOptional()
                    .orElseThrow(() -> new WatchListServiceFail("No user logged in!"));

            MovieEntity movie = movieService.addOrGetmovieEntityFromDTO(movieDTO)
                    .orElseThrow(() -> new WatchListServiceFail("Failed adding movie to DB!"));

            if (myUser.getWatchList().contains(movie)) {
                throw new WatchListServiceFail("Movie is already in WatchList!");
            }


            myUser.getWatchList().add(movie);
            movie.getWatchList().add(myUser);


           userRepository.save(myUser);
        } catch (Exception e) {
            throw new WatchListServiceFail(e.getMessage());
        }
    }


    @Transactional
    public void removeFromWatchList(TmdbMovieDTO movieDTO) throws WatchListServiceFail {
        try {
            UserEntity myUser = userService.getMyUserOptional()
                    .orElseThrow(() -> new WatchListServiceFail("No user logged in!"));

            MovieEntity movie = movieService.findMovieById(movieDTO.id())
                    .orElseThrow(() -> new WatchListServiceFail("Movie not in DB"));

            if (!myUser.getWatchList().contains(movie)) {
                throw new WatchListServiceFail("Movie is not in WatchList!");
            }


            myUser.getWatchList().remove(movie);
            movie.getWatchList().remove(myUser);


            userRepository.save(myUser);
        }
        catch (Exception e) {
            throw new WatchListServiceFail(e.getMessage());
        }
    }


    @Transactional
    public boolean isInMyWatchList(TmdbMovieDTO movieDTO) throws WatchListServiceFail {

        Optional<UserEntity> myUserOpt = userService.getMyUserOptional();
        if (!myUserOpt.isPresent()) {
            throw new WatchListServiceFail("No user logged in!");
        }

        // No need to addOrGet here. If the movie isnt in the DB then its not in  watch list either...
        Optional<MovieEntity> movieEntityOpt = movieService.findMovieById(movieDTO.id());
        if (!movieEntityOpt.isPresent()) {
           return false;
        }

        UserEntity myUser = myUserOpt.get();
        MovieEntity movie = movieEntityOpt.get();

        return myUser.getWatchList().contains(movie);
    }
    @Transactional(readOnly = true)
    public List<TmdbMovieDTO> getWatchList() throws WatchListServiceFail {
        try {
            UserEntity myUser = userService.getMyUserOptional()
                    .orElseThrow(() -> new WatchListServiceFail("No user logged in!"));


            Hibernate.initialize(myUser.getWatchList());


            return myUser.getWatchList().stream()
                    .map(movieService::convertToTmdbMovieDTO) // <--- Υποθέτουμε ότι υπάρχει αυτή η μέθοδος
                    .collect(Collectors.toList());

        } catch (WatchListServiceFail e) {

            throw e;
        } catch (Exception e) {

            throw new WatchListServiceFail("Failed to retrieve watchlist: " + e.getMessage());
        }
    }
}
