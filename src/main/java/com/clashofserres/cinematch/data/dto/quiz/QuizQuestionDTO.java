package com.clashofserres.cinematch.data.dto.quiz;

public class QuizQuestionDTO {

	private String q;
	private String a;
	private String b;
	private String c;
	private String correct;

	public QuizQuestionDTO() {}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}
}
