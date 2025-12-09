package com.clashofserres.cinematch.repository;

import com.clashofserres.cinematch.data.model.ActorEntity;
import com.clashofserres.cinematch.data.model.MovieEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void whenSaveAndRetrieveMovie_thenSuccess() {
        // Given
        MovieEntity movie = new MovieEntity();
        movie.setId(555L);
        movie.setTitle("Inception");
        movie.setOverview("A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.");
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));

        // When
        MovieEntity savedMovie = movieRepository.save(movie);
        Optional<MovieEntity> retrievedMovieOptional = movieRepository.findById(savedMovie.getId());

        // Then
        assertThat(retrievedMovieOptional).isPresent();
        MovieEntity retrievedMovie = retrievedMovieOptional.get();

        assertThat(retrievedMovie.getId()).isEqualTo(savedMovie.getId());
        assertThat(retrievedMovie.getTitle()).isEqualTo("Inception");
        assertThat(retrievedMovie.getOverview()).isEqualTo("A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.");
        assertThat(retrievedMovie.getReleaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
    }

    @Test
    void whenSaveMovieWithActors_thenRetrieveMovieWithActors() {
        // Given
        ActorEntity actor1 = new ActorEntity();
        actor1.setId(123L);
        actor1.setName("Leonardo DiCaprio");
        entityManager.persist(actor1);

        ActorEntity actor2 = new ActorEntity();
        actor2.setId(456L);
        actor2.setName("Joseph Gordon-Levitt");
        entityManager.persist(actor2);

        MovieEntity movie = new MovieEntity();
        movie.setId(789L);
        movie.setTitle("Inception");
        movie.setOverview("A thief who steals corporate secrets...");
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));
        movie.getCast().add(actor1);
        movie.getCast().add(actor2);

        // When
        movieRepository.save(movie);
        Optional<MovieEntity> retrievedMovieOptional = movieRepository.findById(movie.getId());

        // Then
        assertThat(retrievedMovieOptional).isPresent();
        MovieEntity retrievedMovie = retrievedMovieOptional.get();
        assertThat(retrievedMovie.getCast()).hasSize(2);
        assertThat(retrievedMovie.getCast())
                .extracting(ActorEntity::getName)
                .containsExactlyInAnyOrder(
                        "Leonardo DiCaprio",
                        "Joseph Gordon-Levitt"
                );
    }
}
