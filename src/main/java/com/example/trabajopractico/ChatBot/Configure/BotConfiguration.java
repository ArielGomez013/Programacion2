/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajopractico.ChatBot.Configure;
import com.example.trabajopractico.ChatBot.Bot.BotTelegram;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 *
 * @author ariel
 */

@Configuration
public class BotConfiguration {
      @Bean
    public TelegramBotsApi telegramBotsApi(BotTelegram bot) throws Exception 
    {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        System.out.println("BOT REGISTRADO CORRECTAMENTE");
        return api;
    }
}   

