package com.clashofserres.cinematch.frontend.view;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.clashofserres.cinematch.service.UserService;


public final class RegisterDialog extends Dialog
{
    private final EmailField email;
    private final TextField username;
    private final PasswordField password;

    private final UserService userService;

    public RegisterDialog(UserService userService)
    {
        this.userService = userService;

        setOpened(false);
        setCloseOnEsc(true);
        //setCloseOnOutsideClick(true);
        //setModal(true);
        //setDraggable(true);
        //setResizable(false);
        setMinWidth("25em");
        //addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);


        H2 title = new H2("Register an Account");
        title.getStyle().set("margin", "0 auto")
                .set("text-align", "center");

        VerticalLayout titleLayout = new VerticalLayout(title);
        titleLayout.setPadding(false);
        titleLayout.setSpacing(false);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        add(titleLayout);

        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        email = new EmailField("Email");
        username = new TextField("Username");
        password = new PasswordField("Password");

        email.setRequired(true);
        email.setRequiredIndicatorVisible(true);
        email.setErrorMessage("Please enter a valid email address");
        email.setMinWidth("20em");

        username.setRequired(true);
        username.setRequiredIndicatorVisible(true);
        username.setErrorMessage("Please enter a valid username");
        email.setMinWidth("20em");

        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);
        password.setErrorMessage("Please enter a valid password");
        email.setMinWidth("20em");


        layout.add(email, username, password);

        HorizontalLayout buttons = new HorizontalLayout();
        //buttons.setAlignItems(FlexComponent.Alignment.CENTER);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        Button register = new Button("Register", e ->
        {
            onRegisterClick();
        });
        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        register.getStyle().set("margin-right", "auto");

        Button cancel = new Button("Cancel", e ->
        {
            close();
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        add(layout);

        buttons.add(register, cancel);
        getFooter().add(register, cancel);

    }


    void onRegisterClick()
    {
        if (username.isEmpty()
                || email.isEmpty()
                || password.isEmpty())
        {

            Notification.show("All fields are required!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

            return;
        }

        try{
            userService.registerUser(username.getValue().trim(),
                    email.getValue().trim(),
                    password.getValue().trim());

            Notification.show("Registration successful!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            close();
        }
        catch (UserService.InvalidCredentials e)
        {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            Notification.show("An unknown error occurred. Please try again.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}