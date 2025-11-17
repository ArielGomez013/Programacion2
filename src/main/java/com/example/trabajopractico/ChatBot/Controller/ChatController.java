/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Controller;

import com.example.trabajopractico.ChatBot.Service.GeminiService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author ariel
 */
@RestController
public class ChatController {
    
     private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(required = false) String mensaje) {
    String saludoBot = "Hola, soy un analista de los mejores equipos del mundo. ";

        // Si no mandan mensaje, usa un mensaje por defecto
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = "¿En qué puedo ayudarte hoy?";
        }

        // Agrega el saludo ANTES del mensaje del usuario
        String mensajeFinal = saludoBot + mensaje;

        return geminiService.obtencionDeRespuesta(mensajeFinal);
    }
}
    

