package com.clashofserres.cinematch.frontend.component.quiz;

import com.clashofserres.cinematch.data.dto.quiz.QuizDTO;
import com.clashofserres.cinematch.data.dto.quiz.QuizQuestionDTO;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.notification.Notification;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class QuizDialog extends Dialog
{

	private final Map<QuizQuestionDTO, RadioButtonGroup<String>> answerInputs = new HashMap<>();
	private QuizLayout quizLayout;

	public QuizDialog(Supplier<QuizDTO> quizCallback)
	{

		setWidth("600px");
		setHeight("600px");
		setCloseOnEsc(true);
		//setCloseOnOutsideClick(true);

		quizLayout = new QuizLayout(quizCallback);
		add(quizLayout);
	}
}