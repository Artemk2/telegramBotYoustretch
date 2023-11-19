package org.youstretch.telegram.yclientsapi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.youstretch.telegram.yclientsapi.config.BotConfig;

import java.io.IOException;
import java.io.InputStream;
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
        System.out.println("In chatId =" + chatId + " Text: " + messageText);

        YclientsService yclientsService = new YclientsService();
        String response = null;
        String photoPath ;
        //Сегодняшняя дата
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateToday = today.format(formatter);
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

                //Photo: Юля в белом на шпагате с сердечком
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/7b71039d-0143-411b-8900-6aa1fd8660e2/7b71039d-0143-411b-8900-6aa1fd8660e2-6701173.jpeg";
                sendPhoto(chatId, photoPath);
                response = "https://b911101.yclients.com/company/528085/activity/select?o=act" + formattedDateToday + "\n";
                response = response + "Переходите по ссылке для записи в yclients";
                break;
            case "Адрес студии":
                //Photo: Лена и Вика на кубиках
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/9bb43647-7d97-42e5-bfbf-d59a626a2bf8/9bb43647-7d97-42e5-bfbf-d59a626a2bf8-6701568.jpeg";
                sendPhoto(chatId, photoPath);
                response = """
                        м. Менделеевская
                        г. Москва, ул. Новослободская, д. 26, корп. 1
                        Кабинет 233 (2 подъeзд)
                        Домофон 233
                        Второй этаж""";
                sendMessage(chatId,response);
                response = "Путь от Метро Менделеевская";
                String videoFilePath = "video/pathToYoustretch.MOV";
                Thread thread = new Thread(() -> {
                    // Ваш код, который нужно выполнить параллельно
                    sendLocalVideo(chatId, videoFilePath);
                });
                // Запускаем поток
                thread.start();
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
                response = """
                        ✅Гибкость (60 минут)
                        ✅Здоровая спина (60 минут)
                        \uD83D\uDCAA Рельеф + гибкость (90 минут)
                        ✅Два шпагата (90 минут)
                        \uD83E\uDDD8\u200D♀ Гибкость и ️медитация (90 минут)
                        ✅Рельеф (60 минут)
                        ✅Свободный формат (60-90 минут)
                        ✅Акробатика (60 минут)
                        ✅Танцы (60 минут)
                        ✅Медитация поющими чашами (60 минут)
                        ✅Энергомассаж (60 минут)""";
                break;
            case "Форматы":
                //Photo: Лена на полу
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/684d1f28-006d-4cf6-99b9-cfbe321b0958/684d1f28-006d-4cf6-99b9-cfbe321b0958-6701536.jpeg";
                sendPhoto(chatId, photoPath);
                response = """
                        ☘️Форматы занятий:
                        ☘️Индивидуальные тренировки
                        ☘️Мини группы - 3 человека""";
                break;
            case "Супер SALE":
                //Photo: Скидки ноября
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/c2d645b4-d96b-4ed3-86df-d3d841dc354c/c2d645b4-d96b-4ed3-86df-d3d841dc354c-6711835.jpeg";
                sendPhoto(chatId, photoPath);
                response = "Промокод для оплаты на сайте youstretch.ru: HAPPY15";
                break;
            case "Хочу скидку":
                //Photo: скидки 15
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/46355aa9-25fc-45df-a48e-1d8db5297f74/46355aa9-25fc-45df-a48e-1d8db5297f74-6779161.jpeg";
                sendPhoto(chatId, photoPath);
                response = """
                        Отзыв напиши - скидку получи! 😉
                        Прекрасные наши ученики, мы предлагаем Вам помочь развитию нашей студии ☺️
                        Благодаря Вашим отзывам студия недавно получила от «Яндекса»  звание «ХОРОШЕЕ МЕСТО»🔥- спасибо!
                        
                        Предлагаем оставить отзывы о нашей студии осознанного фитнеса YOU STRETCH. За каждый отзыв скидка 5% на покупку абонемента - наш приятный бонус для каждого! 🫶
                        
                        Для начисления скидки скриншот отзыва необходимо отправить администратору.\s
                        
                        Студия есть в 2Гис, Яндекс картах, гугл картах.\s
                        Скидки СУММИРУЮТСЯ (максимум 15%).
                        
                        🫶 Ссылки ⬇️
                        
                        https://yandex.ru/maps/org/199635230552
                        
                        https://2gis.ru/moscow/geo/70000001059760797
                        
                        https://maps.app.goo.gl/w8vUSbeJTZCFrGqFA?g_st=it
                        """;
                break;
            case "Массаж лица":
                //Photo: Массаж лица
                photoPath = "https://259506.selcdn.ru/sites-static/site615630/3b168d6c-95ea-4f1f-aa87-d7122eb6bb17/3b168d6c-95ea-4f1f-aa87-d7122eb6bb17-6779044.jpeg";
                sendPhoto(chatId, photoPath);
                response = "Группа по массажу лица.\n" +
                        "https://t.me/+HNZs71UZo40xMGFi.";
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

    private void sendLocalVideo(long chatId, String videoFilePath) {
        //Отправка видео
        // Создаём объект для отправки видео
        InputStream videoStream = TelegramBot.class.getClassLoader().getResourceAsStream(videoFilePath);
        if (videoStream != null) {
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId);
//        sendVideo.setVideo(new InputFile(new File(videoFilePath)));
            sendVideo.setVideo(new InputFile(videoStream, "your_video_name"));
            // Установка размеров видео
            sendVideo.setWidth(576);
            sendVideo.setHeight(1280);
            try {
                // Отправляем видео
                execute(sendVideo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                sendErrorMessageTelegramApiException(chatId, e);
            }
        }else {
            System.out.println("Video file not found!");
            sendMessage(chatId,"Видео файл не обнаружен");
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
        KeyboardRow keyboardFortyRow = new KeyboardRow();

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
        keyboardThirdRow.add(new KeyboardButton("Чат\uD83D\uDCAC"));
        keyboardFortyRow.add(new KeyboardButton("Хочу скидку"));
        keyboardFortyRow.add(new KeyboardButton("Массаж лица"));

        //Добавляем все строчки клавиатуры в список
        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);
        keyboardRowList.add(keyboardFortyRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
}
