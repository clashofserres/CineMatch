package com.clashofserres.cinematch.frontend.component.review;

import com.clashofserres.cinematch.data.model.ReviewEntity;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Optional;
import java.util.function.Consumer;

public class MovieReviewInputLayout extends VerticalLayout {

    private Optional<Consumer<String>> submitCallback = Optional.empty();

    private TextArea contentTextArea;
    private Button submitButton;
    private Div logInToReviewText;

    public MovieReviewInputLayout() {
        //setMinWidth("25em");
        //setSpacing(true);
        //addClassNames(LumoUtility.Display.FLEX, LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);

        contentTextArea = new TextArea("Fun Fact: You can submit multiple reviews!");
        contentTextArea.setRequired(true);
        contentTextArea.setMaxLength(ReviewEntity.REVIEW_MAX_LENGHT);
        contentTextArea.setWidth(600, Unit.PIXELS);
        contentTextArea.setMinHeight(150, Unit.PIXELS);
        contentTextArea.setHelperText("0/" + contentTextArea.getMaxLength());
        contentTextArea.setValueChangeMode(ValueChangeMode.EAGER);

        contentTextArea.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(
                            e.getValue().length() + "/" + e.getSource().getMaxLength());
        });
        submitButton = new Button("Submit", e ->
        {
            if (submitCallback.isPresent()) {
                submitCallback.get().accept(contentTextArea.getValue());
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //register.getStyle().set("margin-right", "auto");


        logInToReviewText = new Div("You need to be logged in to submit a review!");
        logInToReviewText.getStyle().set("color", "var(--lumo-error-text-color)");

        add(logInToReviewText, contentTextArea, submitButton);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        submitButton.setVisible(isLoggedIn);
        contentTextArea.setVisible(isLoggedIn);

        logInToReviewText.setVisible(!isLoggedIn);

        if (!isLoggedIn) {
            contentTextArea.clear();
        }
    }

    public void setSubmitCallback(Consumer<String> callback) {
        submitCallback = Optional.of(callback);
    }

    public void resetContent() {
        contentTextArea.setValue("");
    }
}
