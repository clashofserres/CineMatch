package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.config.HuggingFaceConfig;
import com.clashofserres.cinematch.data.dto.TmdbMovieDTO;
import com.clashofserres.cinematch.data.dto.TmdbMovieListResponseDTO;
import com.clashofserres.cinematch.data.dto.huggingface.*;
import com.clashofserres.cinematch.data.dto.quiz.QuizDTO;
import com.clashofserres.cinematch.service.helpers.JSONHelper;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

	public static class QuizServiceException extends RuntimeException {
		public QuizServiceException(String message) { super(message); }
	}

	private final int NUMBER_OF_QUESTIONS = 5;

	private final HuggingFaceConfig huggingFaceConfig;
	private final TmdbService tmdbService;

	private final RestTemplate restTemplate;

	private final ObjectMapper mapper = new ObjectMapper();

	public QuizService(HuggingFaceConfig huggingFaceConfig, TmdbService tmdbService) {
		this.tmdbService = tmdbService;
		this.huggingFaceConfig = huggingFaceConfig;
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



	public QuizDTO tryGenerateQuizWithRetries(TmdbMovieDTO movie, int maxAttempts) {
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {

			try {
				QuizDTO quiz = generateQuizForMovie(movie);

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