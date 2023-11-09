package org.youstretch.telegram.yclientsapi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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

import static java.sql.DriverManager.println;

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
        println("Telegram bot started");
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().trim();

        YclientsService yclientsService = new YclientsService();
        String response = null;
        String photoPath = null;
        //Обработка сообщения
        switch (messageText) {
            case "/start":
                //startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                String name = update.getMessage().getChat().getFirstName();

                //Photo: Шпагат в белом
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/85b66920-ef55-44ed-867d-ee38faba44d9/85b66920-ef55-44ed-867d-ee38faba44d9-6701015.jpeg";
                sendPhoto(chatId, photoPath);

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
                //Photo: Лена у ёлки
                //photoPath = "https://259506.selcdn.ru/sites-static/site615630/50e0b538-8b40-4f0c-9959-ba553045d907/50e0b538-8b40-4f0c-9959-ba553045d907-6701249.jpeg";
                //Photo: Татьяна
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/9f76440a-d973-4f85-ada8-fdb842c9aa4a/9f76440a-d973-4f85-ada8-fdb842c9aa4a-6701720.jpeg";
                sendPhoto(chatId, photoPath);
                response = "Контакт администратора @youStretch";
                break;
            case "На сайт студии":
                //Photo: 3 девушки в ГЭС
                //photoPath = "https://259506.selcdn.ru/sites-static/site615630/e5a75a2e-85f7-4ea3-8edc-4ee8d42c8794/e5a75a2e-85f7-4ea3-8edc-4ee8d42c8794-6701338.jpeg";
                //Photo: 3 девушки на белом полу
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/115e00c2-4c30-4665-a531-abeefac3c161/115e00c2-4c30-4665-a531-abeefac3c161-6701445.jpeg";
                sendPhoto(chatId, photoPath);
                response = "youstretch.ru";
                break;
            case "Записаться онлайн":
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = today.format(formatter);
                System.out.println(formattedDate);

                //Photo: Юля в белом на шпагате с сердечком
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/7b71039d-0143-411b-8900-6aa1fd8660e2/7b71039d-0143-411b-8900-6aa1fd8660e2-6701173.jpeg";
                sendPhoto(chatId, photoPath);
                response = "https://b911101.yclients.com/company/528085/activity/select?o=act" + formattedDate + "\n";
                response = response + "Переходите по ссылки для записи в yclients";
                break;
            case "Адрес студии":
                //Photo: Лена и Вика на кубиках
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/9bb43647-7d97-42e5-bfbf-d59a626a2bf8/9bb43647-7d97-42e5-bfbf-d59a626a2bf8-6701568.jpeg";
                sendPhoto(chatId, photoPath);
                response = "м. Менделеевская\n" +
                        "г. Москва, ул. Новослободская, д. 26, корп. 1\nКабинет 233 (2 подъeзд)\nДомофон 233\nВторой этаж";
                break;
            case "Пробное занятие":
                //Photo: Даша и Вика в студии
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/f7cb1ba8-2693-4c34-be44-bbef36506fd3/f7cb1ba8-2693-4c34-be44-bbef36506fd3-6701229.jpeg";
                sendPhoto(chatId, photoPath);
                response = "Стоимость 500 рублей";
                break;
            case "/test":
                response = "CompanyId = " + getYclientsCompanyId();
                break;
            case "Купить абонемент":
                //Photo: 3 девушки на белом полу
                //photoPath = "https://259506.selcdn.ru/sites-static/site615630/115e00c2-4c30-4665-a531-abeefac3c161/115e00c2-4c30-4665-a531-abeefac3c161-6701445.jpeg";
                //sendPhoto(chatId, photoPath);
                response = "https://ailfo.tb.ru/price";
                break;
            case "Направления":
                //Photo: Вика, руки в форме сердечка
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/976cf0ee-a1dc-4b4f-934b-9f0384d1228e/976cf0ee-a1dc-4b4f-934b-9f0384d1228e-6701444.jpeg";
                sendPhoto(chatId, photoPath);
                response = "✅Гибкость (60 минут)\n" +
                        "✅Здоровая спина (60 минут)\n" +
                        "\uD83D\uDCAA Рельеф + гибкость (90 минут)\n" +
                        "✅Два шпагата (90 минут)\n" +
                        "\uD83E\uDDD8\u200D♀ Гибкость и ️медитация (90 минут)\n" +
                        "✅Рельеф (60 минут)\n" +
                        "✅Свободный формат (60-90 минут)\n" +
                        "✅Акробатика (60 минут)\n" +
                        "✅Танцы\n" +
                        "✅Медитация поющими чашами\n" +
                        "✅Энергомассаж";
                break;
            case "Форматы":
                //Photo: Лена на полу
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/684d1f28-006d-4cf6-99b9-cfbe321b0958/684d1f28-006d-4cf6-99b9-cfbe321b0958-6701536.jpeg";
                sendPhoto(chatId, photoPath);
                response = "☘️Форматы занятий:\n☘️Индивидуальные тренировки\n☘️Мини группы - 3 человека";
                break;
            case "Найти студию":
                response = "sendVideo";
                //String videoPath = "https://youtu.be/E85_wBc57zY";
                String videoPath = "https://youtu.be/CzY5eKWwVEo";
                sendVideo(chatId, videoPath);
                break;
            case "sendPhoto":
                //Юля на сапе
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/035f36b7-1e6d-4b30-ad9f-3c337671aa36/035f36b7-1e6d-4b30-ad9f-3c337671aa36-5665126.jpeg";
                //response = "sendPhoto";
                sendPhoto(chatId, photoPath);
                break;
            case "Чат\uD83D\uDCAC":
                //Photo: Несколько людей на фоне воды
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/2bd60e00-1c72-48a4-af3b-2a99ab19831d/2bd60e00-1c72-48a4-af3b-2a99ab19831d-6701008.jpeg";
                sendPhoto(chatId, photoPath);
                response = "https://t.me/+aoFPJzY-bP6LyVR3 \n";
                response = response + "В чате общаются клиенты студии и получают информацию о мероприятиях. ";
                response = response + "Пожалуйста, делитесь фотографиями. Присоединяйтесь и вы!";
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

        sendMessage(chatId, response);
        //end
    }

    private void sendVideo(long chatId, String videoPath) {
        //Отправка видео
        try {
            // Создаем объект для отправки видео
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId);
            sendVideo.setVideo(new InputFile(videoPath));

            // Отправляем видео
            execute(sendVideo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendErrorMessageTelegramApiException(chatId, e);
        }
    }


    private void sendPhoto(long chatId, String photoPath) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        if (photoPath.isBlank()) {
            // error message
            sendMessage(chatId, "Путь к файлу не задан");
        } else {
            // Указываем путь к фотографии (локальный или URL)
            sendPhoto.setPhoto(new InputFile(photoPath));

            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                // Обработка ошибки отправки фотографии
            }
        }
    }

    //sendErrorMessage
    private void sendErrorMessageIOException(long chatId, IOException e) {
        String error = String.valueOf(e);
        errorMessage(chatId, error);
    }

    private void sendErrorMessageTelegramApiException(long chatId, TelegramApiException e) {
        String error = String.valueOf(e);
        errorMessage(chatId, error);
    }

    private void errorMessage(long chatId, String error) {
        String message = "Произошла ошибка при работе telegram бота yclients:\n";
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
