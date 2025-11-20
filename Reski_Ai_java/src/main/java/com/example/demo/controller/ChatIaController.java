package com.example.demo.controller;

import com.example.demo.dto.request.ChatIaRequest;
import com.example.demo.dto.response.ChatIaResponse;
import com.example.demo.service.IotIaClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatIaController {

    private final IotIaClientService iotClient;

    public ChatIaController(IotIaClientService iotClient) {
        this.iotClient = iotClient;
    }

    @PostMapping
    public ResponseEntity<ChatIaResponse> chat(@RequestBody ChatIaRequest req) {
        String resposta = iotClient.gerarTrilha(req.mensagem());
        return ResponseEntity.ok(new ChatIaResponse(resposta));
    }
}
