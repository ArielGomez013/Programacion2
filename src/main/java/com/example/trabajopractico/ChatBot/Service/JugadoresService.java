/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ariel
 */
@Component
public class JugadoresService {

    private Map<String, Map<String, List<String>>> equipos;
    private final ObjectMapper mapper = new ObjectMapper();

    public JugadoresService() {
        cargarJson();
    }

    private void cargarJson() {
        try {
            File f = new File("jugadores.json");
            equipos = mapper.readValue(f, new TypeReference<Map<String, Map<String, List<String>>>>(){});
        } catch (Exception e) {
            equipos = new HashMap<>();
            System.out.println("No se pudo cargar jugadores.json");
        }
    }

    public List<String> getJugadores(String equipo) {
        Map<String, List<String>> data = equipos.get(equipo.toLowerCase());
        if (data == null) return null;
        return data.get("jugadores");
    }
}