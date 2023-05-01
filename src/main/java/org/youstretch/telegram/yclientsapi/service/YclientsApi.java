package org.youstretch.telegram.yclientsapi.service;

//public class YclientsApi {
//}

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YclientsApi {
    @GET("/api/v1/staffs")
    Call<String> getStaffs(@Query("company") String companyId, @Query("client_id") String clientId);
//    @GET("/api/v1/book_services/")
//    Call<String> getBookServices(@Query("company_id") String companyId);

    //Получить список услуг доступных для бронирования
    //company_id:[number] ID компании;
    //staff_id:[number] ID сотрудника. Фильтр по идентификатору сотрудника Default: 0
    //datetime:[number] дата (в формате iso8601). Фильтр по дате бронирования услуги (например '2005-09-09T18:30') Default: ''
    //service_ids[]:[Array of numbers] ID услуг. Фильтр по списку идентификаторов уже выбранных (в рамках одной записи) услуг. Имеет смысл если задан фильтр по мастеру и дате.
    @GET("/api/v1/book_services/{company_id}")
    Call<String> getBookServices(
            //@Path("company_id") String companyId,
            @Path("company_id") Integer companyId,
            @Header("Authorization") String partnerToken,
            @Header("Accept") String acceptHeader,
//            @Header("staff_id") Integer staffId,
//            @Header("date") String date,
            @Header("Content-Type") String contentTypeHeader
    );

}
