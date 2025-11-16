/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Bot;

import com.example.trabajopractico.ChatBot.Service.GeminiService;
import com.example.trabajopractico.ChatBot.Historial.HistorialDeConversacion;
import com.example.trabajopractico.ChatBot.Historial.Usuario;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.IOException;
import java.io.InputStream;
//import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import java.util.HashMap;
import java.util.Map;
//import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.api.objects.InputFile;
/**
 *
 * @author ariel
 */
@Component
public class BotTelegram extends TelegramLongPollingBot {

    private final GeminiService geminiService;
    private final Usuario userStorage;
    private final HistorialDeConversacion historialStorage;

    // Marca si un chat está en "esperando nombre" después de /start
    private final Map<Long, Boolean> esperandoNombre = new HashMap<>();

    public BotTelegram(GeminiService geminiService, Usuario userStorage, HistorialDeConversacion historialStorage) {
        this.geminiService = geminiService;
        this.userStorage = userStorage;
        this.historialStorage = historialStorage;
    }

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() { return "EquiposDelMundoChatBot"; }
    @Override
    public String getBotToken() {
           return botToken;
 }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String texto = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();

            // Guardamos en historial lo que escribe el usuario
            historialStorage.agregar(chatId, "Usuario: " + texto);

            // /start -> pedimos nombre
            if (texto.equalsIgnoreCase("/start")) {
                esperandoNombre.put(chatId, true);
                enviarTexto(chatId, "Hola, soy un bot que habla sobre equipos del mundo\n¿Cuál es tu nombre?");
                return;
            }

            // Si estamos esperando el nombre, guardamos en usuarios
            if (esperandoNombre.getOrDefault(chatId, false)) {
                userStorage.guardarUsuario(chatId, texto);
                esperandoNombre.put(chatId, false);
                enviarTexto(chatId, "Encantado, " + texto + "! Preguntame por equipos o países.");
                return;
            }

            // Envío de escudo si se lo menciona
            enviarEscudoSiCorresponde(chatId, texto);

            // Se llamaa Gemini
            String respuesta = geminiService.obtencionDeRespuesta(texto);

            // Guarda la respuesta en el historial
            historialStorage.agregar(chatId, "Bot: " + respuesta);

            // Si conocemos el nombre lo anteponemos
            String nombre = userStorage.obtenerUsuario(chatId);
            String salida = nombre.isEmpty() ? respuesta : (nombre + ", " + respuesta);

            enviarTexto(chatId, salida);
        }
    }

    // Método auxiliar para mandar texto
    private void enviarTexto(long chatId, String texto) {
        SendMessage m = new SendMessage();
        m.setChatId(String.valueOf(chatId));
        m.setText(texto);
        try {
            execute(m); 
        }
        catch (TelegramApiException e) {
            e.printStackTrace(); 
        }
    }

    private void enviarEscudoSiCorresponde(long chatId, String texto) {
    // Mapa de equipos → nombre del archivo PNG en resources/escudos/
    Map<String, String> escudos = Map.of(
        "real madrid", "realmadrid.png",
        "barcelona", "barcelona.png",
        "manchester united", "manchesterunited.png",
        "juventus", "juventus.png",
        "boca juniors", "boca.png",
        "river plate", "river.png",
        "psg", "psg.png"
    );
    String textoLower = texto.toLowerCase();


for (Map.Entry<String, String> e : escudos.entrySet()) {
if (textoLower.contains(e.getKey())) {
// Usa un try-with-resources para asegurar el cierre de recursos
try (InputStream inputStream = getClass()
.getClassLoader()
.getResourceAsStream("escudos/" + e.getValue())) {

if (inputStream == null) {
enviarTexto(chatId, "No encontré el archivo del escudo: " + e.getValue());
return;
}

// 1. Crea un archivo temporal para escribir el contenido
File tempFile = File.createTempFile("telegram-photo-", ".png");
tempFile.deleteOnExit(); // Asegura que se borre al salir

// 2. Copia el contenido del stream al archivo temporal
try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
inputStream.transferTo(outputStream); // Utiliza Java 9+ transferTo
}

// 3. Usa el archivo temporal para InputFile
SendPhoto photo = new SendPhoto();
photo.setChatId(String.valueOf(chatId));
// Ahora usamos el constructor con File:
photo.setPhoto(new InputFile(tempFile, e.getValue())); 
photo.setCaption("Escudo de " + capitalize(e.getKey()));

execute(photo);

// 4. Borra el archivo temporal inmediatamente después de enviar
tempFile.delete(); 

} catch (TelegramApiException | IOException ex) {
enviarTexto(chatId, "No pude enviar el escudo de " + capitalize(e.getKey()) + ". Error de E/S o Telegram.");
ex.printStackTrace();
}
               return;
}
 }
}
/*
    String textoLower = texto.toLowerCase();

    for (Map.Entry<String, String> e : escudos.entrySet()) {
        if (textoLower.contains(e.getKey())) {
            try {

                // ⭐⭐ Cargar archivo correctamente dentro del JAR (Render)
                InputStream inputStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("escudos/" + e.getValue());

                if (inputStream == null) {
                    enviarTexto(chatId, "No encontré el archivo del escudo: " + e.getValue());
                    return;
                }

                SendPhoto photo = new SendPhoto();
                photo.setChatId(String.valueOf(chatId));
                photo.setPhoto(new InputFile(inputStream, e.getValue()));
                photo.setCaption("Escudo de " + capitalize(e.getKey()));

                execute(photo);

            } catch (TelegramApiException ex) {
                enviarTexto(chatId, "No pude enviar el escudo de " + capitalize(e.getKey()));
                ex.printStackTrace();
            }
            return;
        }
    }
}
*/

// Método auxiliar para capitalizar nombres
private String capitalize(String str) {
    String[] palabras = str.split(" ");
    StringBuilder sb = new StringBuilder();
    for (String p : palabras) {
        sb.append(p.substring(0, 1).toUpperCase()).append(p.substring(1)).append(" ");
    }
    return sb.toString().trim();
}
}
