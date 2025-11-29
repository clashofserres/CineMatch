package com.clashofserres.cinematch.data.dto.huggingface;

public class HuggingFaceChoiceDTO {

	private int index;
	private HuggingFaceChatMessageDTO message;

	public HuggingFaceChoiceDTO() {}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public HuggingFaceChatMessageDTO getMessage() {
		return message;
	}

	public void setMessage(HuggingFaceChatMessageDTO message) {
		this.message = message;
	}
}
