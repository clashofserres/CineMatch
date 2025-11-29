package com.clashofserres.cinematch.data.dto.huggingface;

import java.util.List;

public class HuggingFaceRequestDTO {

	private String model;
	private List<HuggingFaceChatMessageDTO> messages;
	private Integer max_tokens;

	public HuggingFaceRequestDTO() {}

	public HuggingFaceRequestDTO(String model, List<HuggingFaceChatMessageDTO> messages, Integer max_tokens) {
		this.model = model;
		this.messages = messages;
		this.max_tokens = max_tokens;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<HuggingFaceChatMessageDTO> getMessages() {
		return messages;
	}

	public void setMessages(List<HuggingFaceChatMessageDTO> messages) {
		this.messages = messages;
	}

	public Integer getMax_tokens() {
		return max_tokens;
	}

	public void setMax_tokens(Integer max_tokens) {
		this.max_tokens = max_tokens;
	}
}
