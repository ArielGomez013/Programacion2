/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.example.trabajopractico.ChatBot;

import com.example.trabajopractico.ChatBot.Bot.BotTelegram;
import com.example.trabajopractico.ChatBot.Service.GeminiService;
import com.example.trabajopractico.TrabajopracticoApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
/**
 *
 * @author ariel
 */
public class TestChatBot {

    public static void main(String[] args) {
        try{
        
            ApplicationContext context = SpringApplication.run(TrabajopracticoApplication.class, args);
            
            GeminiService geminiService = context.getBean(GeminiService.class);

            BotTelegram TelegramBot= context.getBean(BotTelegram.class);
            
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(TelegramBot);

            System.out.println("Bot registrado y servidor Spring levantado correctamente");
 }
        catch (TelegramApiException e){
            e.printStackTrace();        
}
        
}
               
}

    
    
    

