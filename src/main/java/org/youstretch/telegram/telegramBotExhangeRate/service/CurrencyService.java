package org.youstretch.telegram.telegramBotExhangeRate.service;

import org.json.JSONObject;
import org.youstretch.telegram.telegramBotExhangeRate.model.CurrencyModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class CurrencyService {

    private static final String API_URL = "https://www.nbrb.by/api/exrates/rates/";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";

    public static String getCurrencyRate(String currencyCode, CurrencyModel model) throws IOException {
        String apiUrl = API_URL + currencyCode + "?parammode=2";
        URL url = new URL(apiUrl);
        InputStream contentStream = url.openStream();
        Scanner scanner = new Scanner(contentStream, "UTF-8");
        String result = scanner.useDelimiter("\\Z").next();
        scanner.close();
        JSONObject object = new JSONObject(result);

        model.setCur_ID(object.getInt("Cur_ID"));
        try {
            model.setDate(new SimpleDateFormat(DATE_FORMAT).parse(object.getString("Date")));
        } catch (java.text.ParseException e) {
            System.out.println("Ошибка парсинга даты: " + e.getMessage());
            throw new RuntimeException(e);
        }

        model.setCur_Abbreviation(object.getString("Cur_Abbreviation"));
        model.setCur_Scale(object.getInt("Cur_Scale"));
        model.setCur_Name(object.getString("Cur_Name"));
        model.setCur_OfficialRate(object.getDouble("Cur_OfficialRate"));

        return String.format("Official rate of BYN to %s\non the date: %s\nis: %s BYN per %d %s",
                model.getCur_Abbreviation(), formatDate(model), model.getCur_OfficialRate(),
                model.getCur_Scale(), model.getCur_Abbreviation());
    }

    private static String formatDate(CurrencyModel model) {
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getDate());
    }
}
