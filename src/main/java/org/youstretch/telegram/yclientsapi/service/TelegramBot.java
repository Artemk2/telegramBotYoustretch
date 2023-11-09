package org.youstretch.telegram.yclientsapi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.youstretch.telegram.yclientsapi.config.BotConfig;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                    response = yclientsService.getBookServices(companyId, partnerToken);
                } catch (IOException e) {
                    sendErrorMessageIOException(chatId, e);
                    throw new RuntimeException(e);
                }
                break;
            case "Админ\uD83D\uDC67":
                response = "Контакт администратора @youStretch";
                break;
            case "На сайт студии":
                response = "youstretch.ru";
                break;
            case "Записаться онлайн":
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = today.format(formatter);
                System.out.println(formattedDate);
                response = "https://b911101.yclients.com/company/528085/activity/select?o=act" + formattedDate;
                break;
            case "Адрес студии":
                response = "м. Менделеевская\n" +
                        "г. Москва, ул. Новослободская, д. 26, корп. 1\nКабинет 233 (2 подъeзд)\nДомофон 233\nВторой этаж";
                break;
            case "Пробное занятие":
                response = "600 рублей";
                break;
            case "/test":
                response = "CompanyId = " + getYclientsCompanyId();
                break;
            case "Купить абонемент":
                response = "https://ailfo.tb.ru/price";
                break;
            case "Направления":
                response = "1️⃣Гибкость (60 минут)\n2️⃣Здоровая спина (60 минут)\n\uD83D\uDCAA Рельеф + гибкость (90 минут)\n" +
                        "Два шпагата (90 минут)\n\uD83E\uDDD8\u200D♀ Гибкость и ️медитация (90 минут)\nРельеф (60 минут)\n" +
                        "Свободный формат (60-90 минут)\n" +
                        "Акробатика (60 минут)\nТанцы\nМедитация поющими чашами";
                break;
            case "Форматы":
                response = "Форматы занятий:\nИндивидуальные тренировки\nМини группы - 3 человека";
                break;
            case "Найти студию":
                response = "sendVideo";
                break;
            case "Чат\uD83D\uDCAC":
                response = "https://t.me/+aoFPJzY-bP6LyVR3";
                break;
            case "Получить тренеров":
                try {
                    response = yclientsService.getStaffs(messageText);
                } catch (IOException e) {
                    sendErrorMessageIOException(chatId, e);
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
        if (response == null) {
            response = "ошибка в работе бота.\nОтветное сообщение не подготовлено";
        }
        if (!response.equals("sendVideo")) {
            sendMessage(chatId, response);
        } else {
            // Путь к видеофайлу
            String videoFilePath = "src/main/resources/videoRoute.mp4";
            //Отправка видео
            try {
                // Создаем объект для отправки видео
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatId);
                sendVideo.setVideo(new InputFile(videoFilePath));

                // Отправляем видео
                execute(sendVideo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                sendErrorMessageTelegramApiException(chatId, e);
            }
        }

    }


    private void sendErrorMessageIOException(long chatId, IOException e) {
        String error = String.valueOf(e);
        errorMessage(chatId,error);
    }private void sendErrorMessageTelegramApiException(long chatId, TelegramApiException e) {
        String error = String.valueOf(e);
        errorMessage(chatId,error);
    }
    private void errorMessage(long chatId, String error){
        String message = "Произошла ошибка при работе telegram бота yclients:\n" ;
        sendMessage(chatId, message);
        sendMessage(chatId, error);
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + ", я - бот студии You Stretch!\nХотите записаться на занятие? Я помогу Вам выбрать подходящее время и забронировать место. \nКакое время и дата Вам удобны?";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        System.out.println("chatId=" + chatId + ", textToSend=" + textToSend);
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
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        //keyboardFirstRow.add(new KeyboardButton("/start"));
        //keyboardFirstRow.add(new KeyboardButton("список услуг"));
        //keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("На сайт студии"));
        //keyboardFirstRow.add(new KeyboardButton("Контакты"));
        keyboardFirstRow.add(new KeyboardButton("Админ\uD83D\uDC67"));
        keyboardFirstRow.add(new KeyboardButton("Форматы"));
        keyboardSecondRow.add(new KeyboardButton("Записаться онлайн"));
        keyboardSecondRow.add(new KeyboardButton("Адрес студии"));
        keyboardSecondRow.add(new KeyboardButton("Пробное занятие"));
        keyboardThirdRow.add(new KeyboardButton("Купить абонемент"));
        keyboardThirdRow.add(new KeyboardButton("Направления"));
        //keyboardThirdRow.add(new KeyboardButton("Найти студию"));
        keyboardThirdRow.add(new KeyboardButton("Чат\uD83D\uDCAC"));

        //Добавляем все строчки клавиатуры в список
        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
}
