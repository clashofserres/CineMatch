package com.clashofserres.cinematch.data.dto.quiz;

import java.util.List;

public class QuizDTO {

	private List<QuizQuestionDTO> questions;

	public QuizDTO() {}

	public QuizDTO(List<QuizQuestionDTO> questions) {
		this.questions = questions;
	}

	public List<QuizQuestionDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuizQuestionDTO> questions) {
		this.questions = questions;
	}
}
