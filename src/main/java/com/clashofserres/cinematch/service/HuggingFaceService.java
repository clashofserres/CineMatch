package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.config.HuggingFaceConfig;
import com.clashofserres.cinematch.data.dto.huggingface.HuggingFaceChatMessageDTO;
import com.clashofserres.cinematch.data.dto.huggingface.HuggingFaceRequestDTO;
import com.clashofserres.cinematch.data.dto.huggingface.HuggingFaceResponseDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HuggingFaceService {

    public static class HuggingFaceServiceException extends RuntimeException {
        public HuggingFaceServiceException(String message) { super(message); }
    }
    private final HuggingFaceConfig huggingFaceConfig;
    private final RestTemplate restTemplate;

    private final Integer MAX_TOKENS = 10000;

    HuggingFaceService(HuggingFaceConfig huggingFaceConfig) {
        this.huggingFaceConfig = huggingFaceConfig;
        this.restTemplate = new RestTemplate();
    }

    public HuggingFaceResponseDTO sendRequest(String prompt) throws HuggingFaceServiceException {

        ResponseEntity<HuggingFaceResponseDTO> response = restTemplate.exchange(
                huggingFaceConfig.getBaseUrl() + "chat/completions",
                HttpMethod.POST,
                buildRequestHtppEntity(prompt),
                HuggingFaceResponseDTO.class
        );

        HuggingFaceResponseDTO body = response.getBody();

        if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
            throw new HuggingFaceServiceException("No valid completion returned from HuggingFace.");
        }

        return body;
    }

    public String responseToText(HuggingFaceResponseDTO response) {
        String content = response
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
        return content;
    }

    private HttpEntity<HuggingFaceRequestDTO> buildRequestHtppEntity(String prompt) {
        return new HttpEntity<>(
                buildRequest(prompt),
                buildHeaders());
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(huggingFaceConfig.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private HuggingFaceRequestDTO buildRequest(String prompt) {
        List<HuggingFaceChatMessageDTO> list = List.of(
                new HuggingFaceChatMessageDTO("system", "/no_think"),
                new HuggingFaceChatMessageDTO("user", prompt));

        return new HuggingFaceRequestDTO(huggingFaceConfig.getModel(), list, MAX_TOKENS);
    }
}
