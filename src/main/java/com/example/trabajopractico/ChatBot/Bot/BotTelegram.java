/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Bot;

import com.example.trabajopractico.ChatBot.Service.GeminiService;
import com.example.trabajopractico.ChatBot.Historial.HistorialDeConversacion;
import com.example.trabajopractico.ChatBot.Historial.Usuario;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.ByteArrayInputStream; // Necesitas este import
import java.io.IOException;
import org.springframework.util.StreamUtils; // Necesitas este import si no lo tienes
import java.nio.file.Files;
import java.io.IOException;
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
            
            // Usamos try-with-resources para el InputStream
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("/escudos/" + e.getValue())) {

                if (inputStream == null) {
                    enviarTexto(chatId, "❌ No se encontró el recurso del escudo dentro del JAR.");
                    return;
                }
                
                // 1. Cargar todo el contenido del InputStream a un array de bytes
                byte[] fileContent = StreamUtils.copyToByteArray(inputStream);
                
                // 2. Crear un nuevo InputStream a partir del array de bytes (en memoria)
                // Esto permite que Telegram lea el contenido sin depender del disco
                ByteArrayInputStream bais = new ByteArrayInputStream(fileContent);

                // 3. Preparar el mensaje SendPhoto
                SendPhoto photo = new SendPhoto();
                photo.setChatId(String.valueOf(chatId));
                // Usamos el constructor de InputFile con el nuevo stream en memoria
                photo.setPhoto(new InputFile(bais, e.getValue())); 
                photo.setCaption("Escudo de " + capitalize(e.getKey()));

                // 4. Enviamos la foto
                execute(photo);
                
                // Cerramos el ByteArrayInputStream (aunque se cierra al salir del try, es buena práctica)
                bais.close();

            } catch (TelegramApiException ex) {
                enviarTexto(chatId, "⚠️ Error de Telegram al subir el escudo de " + capitalize(e.getKey()) + ".");
                ex.printStackTrace();
            } catch (IOException ex) {
                enviarTexto(chatId, "⚠️ Error de I/O al cargar el recurso a memoria: " + capitalize(e.getKey()));
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
