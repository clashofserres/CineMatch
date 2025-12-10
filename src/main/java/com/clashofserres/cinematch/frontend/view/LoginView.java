package com.clashofserres.cinematch.frontend.view;

import com.clashofserres.cinematch.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "login", autoLayout = false)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends Main implements BeforeEnterObserver {

    private final UserService userService;
    private final LoginOverlay loginOverlay;
    private final RegisterDialog registerDialog;

    public LoginView(UserService userService) {
        this.userService = userService;

        addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.AlignItems.CENTER);
        setSizeFull();

        loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("CineMatch");
        loginOverlay.setDescription("Welcome to CineMatch!");
        loginOverlay.setAction("login");
        loginOverlay.setForgotPasswordButtonVisible(false);

        registerDialog = new RegisterDialog(userService);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        
        Button registerButton = new Button("Don't have an account yet?", event -> {
            registerDialog.open();
        });
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        layout.add(registerButton);
        
        loginOverlay.getFooter().add(layout);

        add(loginOverlay, registerDialog);

        loginOverlay.setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginOverlay.setError(true);
        }
    }
}
