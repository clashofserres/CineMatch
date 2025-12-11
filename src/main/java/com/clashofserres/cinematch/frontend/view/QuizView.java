package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.frontend.core.MainLayout;
import com.clashofserres.cinematch.service.QuizService; // Adjust package to match your actual Service
import com.clashofserres.cinematch.frontend.component.quiz.QuizLayout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import java.util.Optional;
import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.service.UserService;

@PageTitle("Movies Quiz")
@Route("quiz")
@Menu(order = 2, icon = LineAwesomeIconUrl.QUESTION_CIRCLE, title = "Movies Quiz")
//@PermitAll
@AnonymousAllowed
public class QuizView extends VerticalLayout {

	private final QuizService quizService;
    private final UserService userService;

	public QuizView(QuizService quizService,UserService userService) {
		this.quizService = quizService;
        this.userService = userService;

		setSizeFull();
		setPadding(false); // Let QuizLayout handle the padding
		setSpacing(false);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        Optional<UserEntity> currentUser = userService.getMyUserOptional();

		QuizLayout quizLayout = new QuizLayout(() -> {
            if (currentUser.isPresent()) {
                // Αν είναι συνδεδεμένος χρήστης -> Προσωποποιημένο Quiz (βάσει ιστορικού)
                return quizService.generatePersonalizedQuiz(currentUser.get().getId());
            } else {
                // Αν είναι επισκέπτης (Guest) -> Τυχαίο Δημοφιλές Quiz
                return quizService.tryGeneratePopularMovieQuizWithRetries(5);
            }
        },
                // Β: Τι θα γίνει όταν τελειώσει (Consumer - Αποθήκευση)
                (score) -> {
                    if (currentUser.isPresent()) {
                        // Αν είναι χρήστης -> Αποθήκευσε το σκορ στη βάση
                        quizService.saveQuizResult(currentUser.get().getId(), score);
                        System.out.println("Saved score for user: " + currentUser.get().getUsername());
                    } else {
                        // Αν είναι Guest -> Δεν κάνουμε τίποτα (ή απλά print)
                        System.out.println("Guest score: " + score + " (not saved)");
                    }
                }
                );

		add(quizLayout);

	}
}