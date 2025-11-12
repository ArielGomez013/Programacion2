/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Bot;

import com.example.trabajopractico.ChatBot.Service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 *
 * @author ariel
 */
@Component
public class BotTelegram extends TelegramLongPollingBot{
    
   private final GeminiService geminiService;
    
    public BotTelegram(GeminiService geminiService){
           this.geminiService= geminiService;
    }

    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Override
    public String getBotUsername() {
        return "EquiposDelMundoChatBot";
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    

    //8318825232:AAGx3CqUZfvSpZ1pDz8TcDBEQulrPfvkKbA
    @Override
    public void onUpdateReceived(Update update) {
        
        if(update.hasMessage() && update.getMessage().hasText()){
           String texto = update.getMessage().getText();
           long chatId = update.getMessage().getChatId();
           
           String respuesta= geminiService.obtencionDeRespuesta(texto);
           
           SendMessage message= new SendMessage();
           message.setChatId(String.valueOf(chatId));
           message.setText(respuesta);
        
        
        
            try {
                execute(message); 
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }
    }

}

    
    
    
 
