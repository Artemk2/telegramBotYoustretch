package org.youstretch.telegram.telegramBotExhangeRate.service;

import org.json.JSONObject;
import org.springframework.expression.ParseException;
import org.youstretch.telegram.telegramBotExhangeRate.model.CurrencyModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class CurrencyService {

    public static String getCurrencyRate(String message, CurrencyModel model) throws IOException, ParseException {
        URL url = new URL("https://www.nbrb.by/api/exrates/rates/" + message + "?parammode=2");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()){
            result +=scanner.nextLine();
        }
        JSONObject object = new JSONObject(result);

        model.setCur_ID(object.getInt("Cur_ID"));
        try {
            model.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.getString("Date")));
        } catch (java.text.ParseException e) {
            System.out.println("Ошибка парсинга даты: " + e.getMessage());
            // или можно выбросить новое исключение, если это более подходящий вариант для вашей программы
            throw new RuntimeException(e);
        }

        model.setCur_Abbreviation(object.getString("Cur_Abbreviation"));
        model.setCur_Scale(object.getInt("Cur_Scale"));
        model.setCur_Name(object.getString("Cur_Name"));
        model.setCur_OfficialRate(object.getDouble("Cur_OfficialRate"));


        return "Official rate of BYN to " + model.getCur_Abbreviation() + "\n" +
                "on the date: " + getFormatDate(model) + "\n" +
                "is: " + model.getCur_OfficialRate() + " BYN per " + model.getCur_Scale() + " " + model.getCur_Abbreviation();

    }

    private static String getFormatDate(CurrencyModel model) {
        return new SimpleDateFormat("dd MMM yyyy").format(model.getDate());
    }
}
