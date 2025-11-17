/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
/**
 *
 * @author ariel
 */
@Service
public class GeminiService {
    private final ChatClient chatClient;
    
    private static final String ContextoBase = """
        Eres un analista de los mejores equipos del mundo.
        Quiero que respondas con amabilidad y un poco detallado de cada equipo 
        y el por qué lo elegiste.
        Cuando el usuario te pregunte los equipos de un país, vas a decir los 3
        mejores equipos de ese país con los detalles que te acabo de decir.
        Solo vas a contestar eso. 
        Si el usuario te dice algo que no esté relacionado con equipos de fútbol, 
        debes responder que solo hablas de equipos de fútbol.
    """;

    public GeminiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // ------------------------------------------
    // 1) Cargar el JSON como texto desde resources
    // ------------------------------------------
    private String cargarJsonEquipos() {
        try {
            ClassPathResource resource = new ClassPathResource("/jugadores.json");
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "{}"; // Evita errores si no existe
        }
    }

    // ------------------------------------------
    // 2) Método principal: crea el prompt completo
    // ------------------------------------------
    public String obtencionDeRespuesta(String promptUsuario) {

        try {

            // Carga el JSON del proyecto
            String jsonDatos = cargarJsonEquipos();

            // Construye el prompt para Gemini
            String prompt = 
                    ContextoBase +
                    "\nA continuación tienes datos locales en formato JSON. " +
                    "Úsalos como referencia directa cuando correspondan.\n\n" +
                    "=== JSON EQUIPOS ===\n" +
                    jsonDatos +
                    "\n=== FIN JSON ===\n" +
                    "\nPregunta del usuario: " + promptUsuario;

            // Envía a Gemini
            return chatClient
                    .prompt()
                    .system(ContextoBase)
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, el servidor está limitado en este momento (error 429). Intenta nuevamente en unos segundos.";
        }
    }
}