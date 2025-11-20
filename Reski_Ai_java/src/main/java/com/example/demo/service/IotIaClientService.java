package com.example.demo.service;

import com.example.demo.dto.request.ChatIaRequest;
import com.example.demo.dto.response.ChatIaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IotIaClientService {

    private final RestTemplate restTemplate;

    @Value("${iot.api.url}")
    private String iotApiUrl;

    public IotIaClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String gerarTrilha(String pergunta) {
        ChatIaRequest body = new ChatIaRequest(pergunta);

        ResponseEntity<ChatIaResponse> response =
                restTemplate.postForEntity(
                        iotApiUrl + "/ia/chat",
                        body,
                        ChatIaResponse.class
                );

        ChatIaResponse chatResponse = response.getBody();
        return chatResponse != null ? chatResponse.resposta() : "Sem resposta da IA.";
    }
}
