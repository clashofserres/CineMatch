package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.frontend.core.MainLayout;
import com.clashofserres.cinematch.service.QuizService; // Adjust package to match your actual Service
import com.clashofserres.cinematch.frontend.component.quiz.QuizLayout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Movies Quiz")
@Route("quiz")
@Menu(order = 2, icon = LineAwesomeIconUrl.QUESTION_CIRCLE, title = "Movies Quiz")
@PermitAll
public class QuizView extends VerticalLayout {

	private final QuizService quizService;

	public QuizView(QuizService quizService) {
		this.quizService = quizService;

		setSizeFull();
		setPadding(false); // Let QuizLayout handle the padding
		setSpacing(false);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);

		QuizLayout quizLayout = new QuizLayout(() -> {
			return quizService.tryGeneratePopularMovieQuizWithRetries(5);
		});

		add(quizLayout);

	}
}