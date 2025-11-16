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
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author ariel
 */
@Component
public class Usuario {
    private static final String FILE = "usuarios.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<Long, String> usuarios = new HashMap<>();

    public Usuario(){
        cargar(); 
    }

    public void guardarUsuario(Long chatId, String nombre) {
        usuarios.put(chatId, nombre);
        guardar();
    }

    public String obtenerUsuario(Long chatId) {
        return usuarios.getOrDefault(chatId, "");
    }

    private void guardar() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE), usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargar() {
        try {
            File f = new File(FILE);
            if (f.exists()) {
                usuarios = mapper.readValue(f, new TypeReference<Map<Long, String>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

