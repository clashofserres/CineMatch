package com.clashofserres.cinematch.frontend.component.quiz;

import com.clashofserres.cinematch.data.dto.quiz.QuizDTO;
import com.clashofserres.cinematch.data.dto.quiz.QuizQuestionDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@NpmPackage(value = "canvas-confetti", version = "1.5.1")
@JsModule("canvas-confetti/dist/confetti.browser.js")
public class QuizLayout extends VerticalLayout {

	// Service for background tasks
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final VerticalLayout quizContainer = new VerticalLayout();
	private final Supplier<QuizDTO> quizCallback;

	// Game State
	private QuizDTO currentQuiz;
	private int currentQuestionIndex = 0;
	private int score = 0;

	public QuizLayout(Supplier<QuizDTO> quizCallback) {
		this.quizCallback = quizCallback;

		setPadding(true);
		setSpacing(true);
		setWidthFull();
		setHeightFull();

		add(new H3("Quiz"));
		add(quizContainer);

		// Center the content within the main layout
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);

		setConfirmQuizStartState();
	}

	private void setConfirmQuizStartState() {
		quizContainer.removeAll();
		quizContainer.add(new H3("Are you ready to start the quiz?"));
		quizContainer.add(new Span("Please be aware that each quiz is random and AI generated."));

		Button startButton = new Button("Start Quiz", event -> {
			setLoadingState();
			loadQuiz();
		});
		startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		quizContainer.add(startButton);
		centerQuizContainer();
	}

	private void setLoadingState() {
		Image img = new Image("images/loading-kitten.gif", "loading screen kitty");
		// img.setWidth("200px");

		quizContainer.removeAll();
		quizContainer.add(img);
		quizContainer.add(new Span("Generating quiz..."));
		centerQuizContainer();
	}

	/**
	 * Called when the QuizDTO is successfully fetched.
	 * Initializes the game state.
	 */
	private void generateQuizLayout(QuizDTO quizDTO) {
		this.currentQuiz = quizDTO;
		this.currentQuestionIndex = 0;
		this.score = 0;

		showCurrentQuestion();
	}

	/**
	 * Renders the current question based on currentQuestionIndex.
	 */
	private void showCurrentQuestion() {
		quizContainer.removeAll();

		// 1. Check if quiz is finished
		if (currentQuestionIndex >= currentQuiz.getQuestions().size()) {
			showFinalResults();
			return;
		}

		QuizQuestionDTO question = currentQuiz.getQuestions().get(currentQuestionIndex);

		// 2. Progress Indicator
		Span progress = new Span("Question " + (currentQuestionIndex + 1) + " / " + currentQuiz.getQuestions().size());
		progress.getStyle().set("color", "gray");
		progress.getStyle().set("font-size", "0.9em");

		// 3. Question Text
		H4 questionText = new H4(question.getQ());
		questionText.getStyle().set("text-align", "center");

		// 4. Answer Buttons
		VerticalLayout optionsLayout = new VerticalLayout();
		optionsLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
		optionsLayout.setSpacing(true);
		optionsLayout.setWidth("100%");
		optionsLayout.setMaxWidth("500px"); // Prevent buttons from being too wide on desktop

		Button btnA = createAnswerButton("A: " + question.getA(), "A", question);
		Button btnB = createAnswerButton("B: " + question.getB(), "B", question);
		Button btnC = createAnswerButton("C: " + question.getC(), "C", question);

		optionsLayout.add(btnA, btnB, btnC);

		// 5. Assemble
		quizContainer.add(progress, questionText, optionsLayout);
		centerQuizContainer();
	}

	/**
	 * Helper to create an answer button with click logic.
	 */
	private Button createAnswerButton(String label, String answerLetter, QuizQuestionDTO question) {
		Button btn = new Button(label);
		btn.addClickListener(e -> handleAnswer(answerLetter, question.getCorrect()));
		//btn.setTooltipText("Correct answer: " + question.getCorrect());
		return btn;
	}

	/**
	 * Logic to process the answer, show feedback, and advance state.
	 */
	private void handleAnswer(String selectedLetter, String correctLetter) {
		boolean isCorrect = selectedLetter.equalsIgnoreCase(correctLetter);

		if (isCorrect) {
			score++;
			showNotification("Correct!", NotificationVariant.LUMO_SUCCESS);
		} else {
			showNotification("Wrong! The correct answer was " + correctLetter, NotificationVariant.LUMO_ERROR);
		}

		// Advance index
		currentQuestionIndex++;

		// Load next question (no delay needed as notification floats)
		showCurrentQuestion();
	}

	private void showNotification(String text, NotificationVariant variant) {
		Notification notification = Notification.show(text);
		notification.addThemeVariants(variant);
		notification.setPosition(Notification.Position.BOTTOM_CENTER);
		notification.setDuration(2500);
	}

	private void showFinalResults() {
		quizContainer.removeAll();

		int total = currentQuiz.getQuestions().size();
		double percentage = (double) score / total;

		// Determine Emoji and Message based on percentage
		String resultEmoji = getEmojiForScore(percentage);
		String resultMessage = getMessageForScore(percentage);

		// Trigger Confetti if the score is good
		if (percentage > 0.6) {
			fireConfetti();
		}


		H1 emojiDisplay = new H1(resultEmoji);
		emojiDisplay.getStyle().set("font-size", "4rem");
		emojiDisplay.getStyle().set("margin-bottom", "0px");

		H3 title = new H3(resultMessage);
		title.getStyle().set("margin-top", "0.5rem");

		Span scoreText = new Span("You scored " + score + " out of " + total);
		scoreText.getStyle().set("font-size", "1.2em");
		scoreText.getStyle().set("font-weight", "bold");
		scoreText.getStyle().set("color", getScoreColor(percentage));

		Button restartBtn = new Button("Play Again", VaadinIcon.REFRESH.create());
		restartBtn.addClassNames("offset-hovered-element");
		restartBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		restartBtn.addClickListener(e -> {
			setLoadingState();
			loadQuiz();
		});

		Button confettiButton = new Button("confettiButton", VaadinIcon.TRASH.create());
		confettiButton.addClickListener(e-> fireConfetti());

		quizContainer.add(emojiDisplay, title, scoreText, restartBtn/*, confettiButton*/);
		centerQuizContainer();
	}

	private String getEmojiForScore(double percentage) {
		if (percentage == 1.0) return "🏆";
		if (percentage >= 0.8) return "🤩";
		if (percentage >= 0.5) return "😎";
		if (percentage >= 0.2) return "😐";
		return "🙈";
	}

	private String getMessageForScore(double percentage) {
		if (percentage == 1.0) return "Perfect Score!";
		if (percentage >= 0.8) return "Great Job!";
		if (percentage >= 0.5) return "Not Bad!";
		if (percentage >= 0.2) return "Better luck next time.";
		return "Ouch...";
	}

	private String getScoreColor(double percentage) {
		if (percentage >= 0.8) return "var(--lumo-success-color)";
		if (percentage >= 0.5) return "var(--lumo-primary-text-color)";
		return "var(--lumo-error-color)";
	}


	private void fireConfetti() {
		// 'this' is the component HTML element
		getElement().executeJs("""
        const rect = this.getBoundingClientRect();
        window.confetti({
            particleCount: 400,
            spread: 360,
            origin: {
                x: (rect.left + rect.width / 2) / window.innerWidth,
                y: (rect.top + rect.height / 2) / window.innerHeight
            }
        });
    """);
	}

	private void centerQuizContainer() {
		quizContainer.setJustifyContentMode(JustifyContentMode.CENTER);
		quizContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}

	private void loadQuiz() {
		UI currentUI = UI.getCurrent();

		CompletableFuture.supplyAsync(quizCallback, executorService)
				.thenAccept(quiz -> {
					currentUI.access(() -> {
						if (quiz != null && quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
							generateQuizLayout(quiz);
						} else {
							handleErrorState();
						}
					});
				});
	}

	private void handleErrorState() {
		quizContainer.removeAll();
		Button retryButton = new Button("Retry Generation");
		retryButton.setIcon(VaadinIcon.REFRESH.create());
		retryButton.addClickListener(event -> {
			setLoadingState();
			loadQuiz();
		});

		quizContainer.add(new H4("Could not generate a quiz for this movie."), retryButton);
		centerQuizContainer();

		Notification notification = Notification.show("Error generating quiz", 5000, Notification.Position.TOP_STRETCH);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
	}
}