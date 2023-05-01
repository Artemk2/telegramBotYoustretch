package org.youstretch.telegram.yclientsapi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.youstretch.telegram.yclientsapi.config.BotConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getTelegramName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getTelegramToken();
    }
    public String getYclientsCompanyId() {
        return botConfig.getYclientsCompanyId();
    }
    public String getYclientsPartnerToken() {
        return botConfig.getYclientsPartnerToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().trim();

        YclientsService yclientsService = new YclientsService();
        String response = null;

        //Обработка сообщения
        switch (messageText) {
            case "/start":
                //startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                String name = update.getMessage().getChat().getFirstName();
                response = "Привет, " + name + ", я - бот студии You Stretch!\n" +
                        "Хотите записаться на занятие? Я помогу Вам выбрать подходящее время и забронировать место.";
                break;
            case "список услуг":
                try {
                    Integer companyId = Integer.parseInt(getYclientsCompanyId());
                    String partnerToken = getYclientsPartnerToken();
                    response = yclientsService.getBookServices(companyId,partnerToken);
                } catch (IOException e) {
                    sendErrorMessage(chatId, e);
                    throw new RuntimeException(e);
                }
                break;
            case "Спросить администратора":
                response = "Контакт администратора @youStretch";
                break;
            case "На сайт студии":
                response = "youstretch.ru";
                break;
            case "Записаться онлайн":
                response = "https://b911101.yclients.com/company/528085/activity/select?o=act2023-05-02";
                break;
            case "/test":
                response = "CompanyId = "+getYclientsCompanyId();
                break;
            case "Получить тренеров":
                try {
                    response = yclientsService.getStaffs(messageText);
                } catch (IOException e) {
                    sendErrorMessage(chatId, e);
                    throw new RuntimeException(e);
                }
                break;
            case "/help":
                response = "Бот умет обращаться к yclients";
                break;
            default:
                response = "Бот Вас не понял. Дайте новую команду";
                break;
        }

//отправка сообщения
        if (response==null){
            response="ошибка в работе бота.\nОтветное сообщение не подготовлено";
        }
        sendMessage(chatId, response);
    }

    private void sendErrorMessage(long chatId, IOException e) {
        String message = "Произошла ошибка при работе telegram бота yclients";
        sendMessage(chatId, message);
        sendMessage(chatId, e.getMessage());
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + ", я - бот студии You Stretch!\nХотите записаться на занятие? Я помогу Вам выбрать подходящее время и забронировать место. \nКакое время и дата Вам удобны?";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        System.out.println("chatId="+chatId +", textToSend="+ textToSend);
        try {
            setButtonsMainMenu(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при отправке сообщения", e);
        }
    }

    private void sendMessageToReply(Message message, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        /**установка Id клиента**/
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(textToSend);
        try {
            setButtonsMainMenu(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Unable to send message to user", e);
        }
    }

    public void setButtonsMainMenu(SendMessage sendMessage) {
        //инициализируем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        //Надо связать сообщение с клавиатурой
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        //Параметр, который выводит клавиатуру всем или только определённым пользователям
        replyKeyboardMarkup.setSelective(true);
        //Подгонка клавиатуры под количество кнопок. Сделать больше или меньше
        replyKeyboardMarkup.setResizeKeyboard(true);
        //Скрывать клавиатуру после сообщения
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        //Создаём кнопки
        List<KeyboardRow> keyboardRowList = new ArrayList();
        //Инициализируем первую строчку клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        //keyboardFirstRow.add(new KeyboardButton("/start"));
        //keyboardFirstRow.add(new KeyboardButton("список услуг"));
        //keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("На сайт студии"));
        //keyboardFirstRow.add(new KeyboardButton("Контакты"));
        keyboardFirstRow.add(new KeyboardButton("Спросить администратора"));
        keyboardFirstRow.add(new KeyboardButton("Записаться онлайн"));

        //Добавляем все строчки клавиатуры в список
        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
}
