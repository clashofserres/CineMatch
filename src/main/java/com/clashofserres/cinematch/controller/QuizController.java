package com.clashofserres.cinematch.controller;

import com.clashofserres.cinematch.data.dto.quiz.QuizDTO;
import com.clashofserres.cinematch.service.QuizService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController
{
	private final QuizService quizService;

	public QuizController(QuizService quizService) {
		this.quizService = quizService;
	}

	@GetMapping("/generate/movie/{movieId}")
	public QuizDTO generateQuizForMovie(@PathVariable long movieId) {
		return quizService.generateQuizForMovie(movieId);
	}

	@GetMapping("/generate/popular_movies")
	public QuizDTO generatePopularMovieQuiz() {
		return quizService.generatePopularMovieQuiz();
	}
}
