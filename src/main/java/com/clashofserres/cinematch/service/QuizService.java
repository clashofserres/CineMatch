package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.config.HuggingFaceConfig;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
import com.clashofserres.cinematch.data.dto.huggingface.*;
import com.clashofserres.cinematch.data.dto.quiz.QuizDTO;
import com.clashofserres.cinematch.data.model.QuizResultEntity;
import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.data.repository.QuizResultRepository;
import com.clashofserres.cinematch.service.helpers.JSONHelper;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.clashofserres.cinematch.data.model.MovieEntity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Collections;
import java.util.List;


@Service
public class QuizService {

	public static class QuizServiceException extends RuntimeException {
		public QuizServiceException(String message) { super(message); }
	}

	private final int NUMBER_OF_QUESTIONS = 5;

	private final HuggingFaceConfig huggingFaceConfig;
	private final TmdbService tmdbService;
    private final UserService userService;
    private final QuizResultRepository quizResultRepository;
	private final RestTemplate restTemplate;

	private final ObjectMapper mapper = new ObjectMapper();

	public QuizService(HuggingFaceConfig huggingFaceConfig, TmdbService tmdbService, UserService userService, QuizResultRepository quizResultRepository) {
		this.tmdbService = tmdbService;
		this.huggingFaceConfig = huggingFaceConfig;
        this.userService = userService;
        this.quizResultRepository = quizResultRepository;
		this.restTemplate = new RestTemplate();

		mapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
	}

	private String buildMoviePrompt(TmdbMovieDTO movie) {
		return """
            Generate a fun movie quiz about the film "%s" (released %s).
            Create EXACTLY %d questions. No more, no less.

            Your response MUST be a single JSON object with this structure:

            {
              "questions": [
                {
                  "q": "QUESTION TEXT",
                  "a": "ANSWER A",
                  "b": "ANSWER B",
                  "c": "ANSWER C",
                  "correct": "a"
                }
              ]
            }

            Rules:
            - Output ONLY the JSON object, nothing else.
            - The JSON must contain NO line breaks.
            - Prefer using double quotes for JSON keys and string values.
            - Make sure to escape any special characters (such as quotes) in the JSON strings.
            - Avoid using quotation marks in questions and answers where possible.
            - Avoid very obvious questions (director, main actor, release year).
			- Ensure the correct field is ALWAYS one of: "a", "b", or "c" and nothing else.
            - Each question must have 3 answer choices: a, b, and c.
            - No question or answer should be longer than 100 characters.
            - An answer should NOT be a simple yes/no, and never be empty either.
            - Questions MUST only by about THIS movie, and not related movies or other media.
            - Do not ask questions about facts are not true for this movie.
            - Do NOT include markdown, backticks, or explanations.
            """.formatted(movie.title(), movie.releaseDate(),
				NUMBER_OF_QUESTIONS);
	}

	private String buildMultiMoviePrompt(List<TmdbMovieDTO> movies) {
		// 1. Format the list of movies into a readable string for the AI
		// Example: "1. Avatar (2009), 2. Titanic (1997)..."
		String movieListString = movies.stream()
				.map(m -> String.format("- \"%s\" (%s)", m.title(), m.releaseDate()))
				.collect(Collectors.joining("\n"));

		return """
           Here is a list of movies:
           %s

           Generate a "Guess the Movie" quiz using ONLY the movies from the list above.
           Create EXACTLY %d questions.

           For each question:
           1. Pick ONE movie from the list to be the correct answer.
           2. Pick TWO other movies from the list to be the wrong answers.
           3. The "q" (Question) must be a specific plot point, character description, or famous scene that describes the correct movie.
           4. The "a", "b", and "c" options must be the exact TITLES of the movies chosen.

           Your response MUST be a single JSON object with this structure:

           {
             "questions": [
               {
                 "q": "Description of the movie plot or scene",
                 "a": "Movie Title A",
                 "b": "Movie Title B",
                 "c": "Movie Title C",
                 "correct": "a"
               }
             ]
           }

           Rules:
           - Output ONLY the JSON object, nothing else.
           - The JSON must contain NO line breaks.
           - Prefer using double quotes for JSON keys and string values.
           - Escape any special characters (like quotes) inside the strings.
           - Ensure the correct field is ALWAYS one of: "a", "b", or "c" and nothing else.
           - Do not ask questions that could apply to multiple movies in the list.
           - Do NOT include markdown, backticks, or explanations.
           """.formatted(movieListString, NUMBER_OF_QUESTIONS);
	}


	// ---------------------------
	// Build HuggingFace request
	// ---------------------------
	private HuggingFaceRequestDTO buildRequest(String prompt) {
		List<HuggingFaceChatMessageDTO> list = List.of(
				new HuggingFaceChatMessageDTO("system", "/no_think"),
				new HuggingFaceChatMessageDTO("user", prompt));

		return new HuggingFaceRequestDTO(huggingFaceConfig.getModel(), list,10000);
	}

	// ---------------------------
	// Send HF request
	// ---------------------------
	private HuggingFaceResponseDTO sendHuggingFaceRequest(String prompt) {

		HuggingFaceRequestDTO requestDTO = buildRequest(prompt);

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(huggingFaceConfig.getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<HuggingFaceRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

		ResponseEntity<HuggingFaceResponseDTO> response = restTemplate.exchange(
				huggingFaceConfig.getBaseUrl() + "chat/completions",
				HttpMethod.POST,
				entity,
				HuggingFaceResponseDTO.class
		);

		HuggingFaceResponseDTO body = response.getBody();

		if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
			throw new QuizServiceException("No valid completion returned from HuggingFace.");
		}

		return body;
	}

	// ---------------------------
	// Parse JSON into QuizDTO
	// ---------------------------
	private QuizDTO parseQuiz(String rawContent) {
		System.out.println(rawContent);
		try {
			String json = JSONHelper.extractJsonObject(rawContent);
			json = JSONHelper.normalize(json);
			return mapper.readValue(json, QuizDTO.class);

		} catch (Exception e) {
			throw new QuizServiceException(
					"Failed to parse quiz JSON: " + e.getMessage() +
							"\nRaw model output:\n" + rawContent
			);
		}
	}

    // ---------------------------
    // PERSONALIZED QUIZ LOGIC (NEW)
    // ---------------------------

    public QuizDTO generatePersonalizedQuiz(Long userId) {
        try {

            UserEntity user = userService.getUserById(userId).orElse(null);

            if (user == null || user.getWatchList() == null || user.getWatchList().isEmpty()) {
                System.out.println("User has no history. Generating random popular quiz.");
                return tryGeneratePopularMovieQuizWithRetries(3);
            }

            Set<TmdbMovieDTO> watchedMoviesDTOs = user.getWatchList().stream()
                    .map(entity -> {
                        try {
                            // ΠΡΟΣΟΧΗ: Εδώ χρησιμοποιούμε το entity.getId().
                            // Αν το ID στη βάση σου είναι ίδιο με του TMDB (π.χ. 550 για Fight Club), είσαι οκ.
                            return tmdbService.getMovieDetails(entity.getId());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(movie -> movie != null)
                    .collect(Collectors.toSet());

            if (watchedMoviesDTOs.isEmpty()) {
                return tryGeneratePopularMovieQuizWithRetries(3);
            }

            List<Integer> topGenres = getTopGenres(watchedMoviesDTOs);

            if (topGenres.isEmpty()) {
                return tryGeneratePopularMovieQuizWithRetries(3);
            }

            TmdbMovieListResponseDTO responseDTO = tmdbService.getMoviesByGenres(topGenres);
            List<TmdbMovieDTO> genreMovies = responseDTO.results();

            if (genreMovies.isEmpty()) {
                return tryGeneratePopularMovieQuizWithRetries(3);
            }

            Collections.shuffle(genreMovies);
            List<TmdbMovieDTO> selectedMovies = genreMovies.stream().limit(6).toList();

            HuggingFaceResponseDTO hfResponse = sendHuggingFaceRequest(buildMultiMoviePrompt(selectedMovies));
            if (hfResponse != null && hfResponse.getChoices() != null && !hfResponse.getChoices().isEmpty()) {
                String content = hfResponse.getChoices().get(0).getMessage().getContent();
                return parseQuiz(content);
            }
            return tryGeneratePopularMovieQuizWithRetries(3);

        } catch (Exception e) {
            e.printStackTrace();
            // Αν κάτι πάει στραβά, γυρνάμε στο default
            return tryGeneratePopularMovieQuizWithRetries(3);
        }
    }


    private List<Integer> getTopGenres(Set<TmdbMovieDTO> watchedMovies) {

        Map<Integer, Long> genreFrequency = watchedMovies.stream()
                .filter(m -> m.genreIds() != null)
                .flatMap(m -> m.genreIds().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        return genreFrequency.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // ---------------------------
    // SAVE RESULTS (NEW)
    // ---------------------------
    public void saveQuizResult(Long userId, int score) {
        QuizResultEntity result = new QuizResultEntity(userId, score);
        quizResultRepository.save(result);
    }



	// ---------------------------
	// Generate Quiz
	// ---------------------------
	public QuizDTO generateQuizForMovie(TmdbMovieDTO movieDTO) {

		HuggingFaceResponseDTO hfResponse = sendHuggingFaceRequest(buildMoviePrompt(movieDTO));

		// Extract model output
		String content = hfResponse
				.getChoices()
				.get(0)
				.getMessage()
				.getContent();

		return parseQuiz(content);
	}

	public QuizDTO generateQuizForMovie(long movieId) {
		try {
			TmdbMovieDTO movieDTO = tmdbService.getMovieDetails(movieId);
			return generateQuizForMovie(movieDTO);
		}
		catch (TmdbService.TmdbServiceException e) {
			throw new QuizServiceException("Failed to retrieve movie details for ID " + movieId + ": " + e.getMessage());
		}
		catch (Exception e) {
			throw new QuizServiceException("Failed to generate quiz for movie ID " + movieId + ": " + e.getMessage());
		}
	}

	public QuizDTO generatePopularMovieQuiz() {

		try {
			TmdbMovieListResponseDTO movieListResponseDTO = tmdbService.getPopularMovies();
			List<TmdbMovieDTO> popularMovies = movieListResponseDTO.results();
			Collections.shuffle(popularMovies);
			//  Take the first 6 elements
			List<TmdbMovieDTO> randomSix = popularMovies.subList(0, 6);
			HuggingFaceResponseDTO hfResponse = sendHuggingFaceRequest(buildMultiMoviePrompt(randomSix));

			// Extract model output
			String content = hfResponse
					.getChoices()
					.get(0)
					.getMessage()
					.getContent();

			return parseQuiz(content);

		}
		catch (TmdbService.TmdbServiceException e) {
			throw new QuizServiceException("Failed to retrieve popular movies: " + e.getMessage());
		}
		catch (Exception e) {
			throw new QuizServiceException("Failed to generate popular movie quiz: " + e.getMessage());
		}
	}

    public List<QuizResultEntity> getUserQuizHistory(Long userId) {
        return quizResultRepository.findByUserId(userId);
    }


	public QuizDTO tryGeneratePopularMovieQuizWithRetries(int maxAttempts) {
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {

			try {
				QuizDTO quiz = generatePopularMovieQuiz();

				if (quiz.getQuestions() != null && quiz.getQuestions().size() >= 5) {
					return quiz; // SUCCESS
				}
			}
			catch (Exception e) {
				// Log if needed, but no need to break — retry
			}
		}

		return null; // FAILED after attempts
	}
}