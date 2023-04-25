package org.youstretch.telegram.telegramBotExhangeRate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.youstretch.telegram.telegramBotExhangeRate.config.BotConfig;
import org.youstretch.telegram.telegramBotExhangeRate.model.CurrencyModel;

import java.io.IOException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().trim();

        if (messageText.equals("/start")) {
            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            return;
        }

        CurrencyModel currencyModel = new CurrencyModel();
        String currency;
        try {
            currency = CurrencyService.getCurrencyRate(messageText, currencyModel);
        } catch (IOException e) {
            sendMessage(chatId, "We have not found such a currency.\nEnter the currency whose official exchange rate you want to know in relation to BYN.\nFor example: USD");
            return;
        }
        sendMessage(chatId, currency);
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!\nEnter the currency whose official exchange rate you want to know in relation to BYN.\nFor example: USD";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Unable to send message to user", e);
        }
    }
}
