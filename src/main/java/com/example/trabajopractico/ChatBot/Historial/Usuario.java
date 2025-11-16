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

    public Usuario() {
        cargar(); // carga segura al iniciar
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
            System.out.println("No se pudo guardar usuarios: " + e.getMessage());
        }
    }

    private void cargar() {
        File f = new File(FILE);
        if (f.exists()) {
            try {
                usuarios = mapper.readValue(f, new TypeReference<Map<Long, String>>() {});
            } catch (IOException e) {
                System.out.println("No se pudo cargar usuarios, inicializando vacío");
                usuarios = new HashMap<>();
            }
        }
    }
}