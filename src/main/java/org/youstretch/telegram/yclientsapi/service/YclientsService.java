package org.youstretch.telegram.yclientsapi.service;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

public class YclientsService {
    private final YclientsApi yclientsAPI;
    //private final ApiConfig apiConfig;

    public YclientsService() {
        //this.apiConfig = apiConfig;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.yclients.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        yclientsAPI = retrofit.create(YclientsApi.class);
    }

    public String getStaffs(String clientId) throws IOException {
        String companyId = "";//  apiConfig.getYclientsCompanyId();
        //String companyId = YclientsConfig.getYclientsCompanyId();
        Call<String> call = yclientsAPI.getStaffs(companyId, clientId);
        Response<String> response = call.execute();

        String errorMessage = checkError(response);
        //обработка ошибки
        if (!(checkError(response).equals("success"))) {
            return errorMessage;
        }
        return response.body();
    }

    public String getBookServices(Integer companyId,String partnerToken) throws IOException {
        setLog(companyId);
        partnerToken = "Bearer "+partnerToken;
        String acceptHeader = "application/vnd.yclients.v2+json";
        String contentTypeHeader = "application/json";
        Integer staffId = 91;
        String date = "2015-09-01";
        //Call<String> call = yclientsAPI.getBookServices(companyId, partnerToken, acceptHeader, staffId, date, contentTypeHeader);
        Call<String> call = yclientsAPI.getBookServices(companyId, partnerToken, acceptHeader, contentTypeHeader);
        Response<String> response = call.execute();
        setLog(response);

        //обработка ошибки
        String errorMessage = checkError(response);
        if (!(errorMessage.equals("success"))) {
            return errorMessage;
        }

        return response.body();
    }

    private void setLog(Response<String> response) {
        System.out.println(
                "response=" + response.raw() + ", response messgage=" + response.message()
        );
    }

    private void setLog(String message) {
        System.out.println("message=" + message);
    }
    private void setLog(Number message) {
        System.out.println("message=" + message);
    }

    public String checkError(Response<String> response) throws IOException {
        String message = "success";
        if (
                !(
                        response.code() == 200 ||
                                response.code() == 202
                )
        ) {
            message = "Произошла ошибка: ";
            switch (response.code()) {
                case 401:
                    message = message + "\n401 Unauthorized («не авторизован (не представился)»)";
                    break;
            }
            System.out.println(message);
            System.out.println(response.errorBody());
        }
        return message;
    }
}
