/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Historial;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ariel
 */
@Component
public class HistorialDeConversacion {

    private static final String FILE = "historial.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<Long, List<String>> historial = new HashMap<>();

    public HistorialDeConversacion() {
        cargar(); // carga segura al iniciar
    }

    public void agregar(Long chatId, String mensaje) {
        historial.computeIfAbsent(chatId, k -> new ArrayList<>()).add(mensaje);
        guardar();
    }

    public List<String> obtener(Long chatId) {
        return historial.getOrDefault(chatId, new ArrayList<>());
    }

    private void guardar() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE), historial);
        } catch (IOException e) {
            System.out.println("No se pudo guardar historial: " + e.getMessage());
        }
    }

    private void cargar() {
        File f = new File(FILE);
        if (f.exists()) {
            try {
                historial = mapper.readValue(f, new TypeReference<Map<Long, List<String>>>() {});
            } catch (IOException e) {
                System.out.println("No se pudo cargar historial, inicializando vacío");
                historial = new HashMap<>();
            }
        }
    }
}