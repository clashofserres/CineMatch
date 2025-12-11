package com.clashofserres.cinematch.frontend.view;
import com.clashofserres.cinematch.data.model.QuizResultEntity;

import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.service.UserService;
import com.clashofserres.cinematch.service.QuizService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("profile")
@PageTitle("My Profile")
@PermitAll
@Menu(order = 4, icon = LineAwesomeIconUrl.USER, title = "Profile")
public class ProfileView extends VerticalLayout {

    private final UserService userService;
    private final QuizService quizService;

    // --- Profile Details Section ---
    private final TextField usernameField = new TextField("Username");
    private final EmailField emailField = new EmailField("Email");
    private final Span watchlistBadge = new Span();


    // --- Password Change Section ---
    private final PasswordField currentPasswordField = new PasswordField("Current Password");
    private final PasswordField newPasswordField = new PasswordField("New Password");

    // --- Quiz History Section ---
    private UserEntity currentUser;

    public ProfileView(UserService userService,QuizService quizService) {
        this.userService = userService;
        this.quizService = quizService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        setDefaultHorizontalComponentAlignment(Alignment.START);

        // Load initial data
        try {
            this.currentUser  = userService.getMyUser();
            usernameField.setValue(currentUser.getUsername());
            emailField.setValue(currentUser.getEmail());

            // --- Badge Logic ---
            int count = (currentUser.getWatchList() != null) ? currentUser.getWatchList().size() : 0;
            watchlistBadge.setText(String.valueOf(count));

            // Reset styles to ensure clean state
            watchlistBadge.getElement().getThemeList().clear();
            watchlistBadge.getStyle().clear();


            watchlistBadge.getElement().getThemeList().add("badge pill");

            if (count >= 10) {

                watchlistBadge.getElement().getThemeList().add("success");
            } else {

                watchlistBadge.getElement().getThemeList().add("primary");
            }

        } catch (Exception e) {
            showNotification("Error loading user data", true);
        }


        add(
                createProfileSection(),
                new Hr(),
                createPasswordSection(),
                new Hr(),
                createQuizHistorySection()
        );
    }

    private VerticalLayout createProfileSection() {
        H3 title = new H3("Personal Information");

        FormLayout form = new FormLayout();
        form.add(usernameField, emailField);

        form.addFormItem(watchlistBadge, "Movies Watched");

        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Button updateProfileBtn = new Button("Update Profile", e -> onUpdateProfile());
        updateProfileBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout section = new VerticalLayout(title, form, updateProfileBtn);
        section.setPadding(false);
        return section;
    }

    private VerticalLayout createPasswordSection() {
        H3 title = new H3("Change Password");

        FormLayout form = new FormLayout();
        form.add(currentPasswordField, newPasswordField);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Button changePassBtn = new Button("Update Password", e -> onChangePassword());
        changePassBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        VerticalLayout section = new VerticalLayout(title, form, changePassBtn);
        section.setPadding(false);
        return section;
    }

    // ---  ΙΣΤΟΡΙΚΟ ---
    private VerticalLayout createQuizHistorySection() {
        H3 title = new H3("Quiz History");


        Grid<QuizResultEntity> grid = new Grid<>(QuizResultEntity.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.setAllRowsVisible(true); // Να φαίνονται όλα χωρίς scroll μέσα στο grid


        grid.addColumn(result -> result.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setHeader("Date Played")
                .setAutoWidth(true);


        grid.addColumn(result -> result.getScore() + " / 5")
                .setHeader("Score")
                .setAutoWidth(true);


        grid.addColumn(new ComponentRenderer<>(result -> {
            Span badge = new Span();
            int score = result.getScore();

            if (score == 5) {
                badge.setText("Perfect! 🏆");
                badge.getElement().getThemeList().add("badge success");
            } else if (score >= 3) {
                badge.setText("Good Job 👍");
                badge.getElement().getThemeList().add("badge success primary");
            } else {
                badge.setText("Try Again 😅");
                badge.getElement().getThemeList().add("badge contrast");
            }
            return badge;
        })).setHeader("Result");


        if (currentUser != null) {
            List<QuizResultEntity> history = quizService.getUserQuizHistory(currentUser.getId());

            history.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            grid.setItems(history);
        }

        VerticalLayout section = new VerticalLayout(title, grid);
        section.setPadding(false);
        return section;
    }
    // ------------------------------------

    private void onUpdateProfile() {
        try {
            userService.updateProfile(usernameField.getValue(), emailField.getValue());
            showNotification("Profile updated successfully!", false);
        } catch (UserService.InvalidCredentials e) {
            showNotification(e.getMessage(), true);
        } catch (Exception e) {
            showNotification("An unexpected error occurred.", true);
        }
    }

    private void onChangePassword() {
        try {
            userService.changePassword(currentPasswordField.getValue(), newPasswordField.getValue());
            showNotification("Password changed successfully!", false);
            currentPasswordField.clear();
            newPasswordField.clear();
        } catch (UserService.InvalidCredentials e) {
            showNotification(e.getMessage(), true);
        } catch (Exception e) {
            showNotification("An unexpected error occurred.", true);
        }
    }

    private void showNotification(String text, boolean isError) {
        Notification notification = Notification.show(text);
        notification.addThemeVariants(isError ? NotificationVariant.LUMO_ERROR : NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }
}