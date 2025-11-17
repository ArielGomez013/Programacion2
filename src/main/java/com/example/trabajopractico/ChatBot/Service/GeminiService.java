/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
/**
 *
 * @author ariel
 */
@Service
public class GeminiService {
    private final ChatClient chatClient;
    
    private static final String ContextoBase="""
    Eres un analista de los mejores equipos del mundo.
    Quiero que respondas con amabilidad y un poco detallado de cada equipo 
    y el por qué lo elegiste.
    Cuando el usuario te preguntes los equipos de un pais, vas a decir los 3
    mejores equipos de ese pais con los detalles que te acabo de decir.
    Solo vas a contestar eso, si el usuario te dice otra cosa que no este
    relacionado con equipos del futbol, le decis que de eso no hablas,
    que hablas solo de equipos de futbol.                       
    """;
    
    public GeminiService(ChatClient.Builder builder){
            this.chatClient= builder.build();
    }
    public String obtencionDeRespuesta(String prompt){
        try{
            return chatClient.prompt()
                    .system(ContextoBase)
                    .user(prompt)
                    .call()
                    .content();
    } catch (Exception e) {
            return "Lo siento, tuve un problema al conectarme con el servidor. Intenta de nuevo más tarde.";
        }
    }
}
