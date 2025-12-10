package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.data.model.UserEntity;
import com.clashofserres.cinematch.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@Route("profile")
@PageTitle("My Profile")
@PermitAll
@Menu(order = 4, icon = LineAwesomeIconUrl.USER, title = "Profile")
public class ProfileView extends VerticalLayout {

    private final UserService userService;

    // --- Profile Details Section ---
    private final TextField usernameField = new TextField("Username");
    private final EmailField emailField = new EmailField("Email");
    private final Span watchlistBadge = new Span();

    // --- Password Change Section ---
    private final PasswordField currentPasswordField = new PasswordField("Current Password");
    private final PasswordField newPasswordField = new PasswordField("New Password");

    public ProfileView(UserService userService) {
        this.userService = userService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        setDefaultHorizontalComponentAlignment(Alignment.START);

        // Load initial data
        try {
            UserEntity user = userService.getMyUser();
            usernameField.setValue(user.getUsername());
            emailField.setValue(user.getEmail());

            // --- Badge Logic ---
            int count = (user.getWatchList() != null) ? user.getWatchList().size() : 0;
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
                createPasswordSection()
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