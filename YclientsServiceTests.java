package com.youstretch.telegram.yclientsapi.service;

import org.junit.Before;
import org.junit.Test;
import org.youstretch.telegram.yclientsapi.service.YclientsApi;
import org.youstretch.telegram.yclientsapi.service.YclientsService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;

public class YclientsServiceTests {
    private YclientsService yclientsService;
    private YclientsApi mockYclientsApi;

    @Before
    public void setUp() {
        mockYclientsApi = mock(YclientsApi.class);
        yclientsService = new YclientsService(mockYclientsApi);
    }

    @Test
    public void testGetStaffs() throws IOException {
        // Подготовка мокового ответа от API
        Call<String> mockCall = mock(Call.class);
        Response<String> mockResponse = Response.success("success response");
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockYclientsApi.getStaffs(anyString(), anyString())).thenReturn(mockCall);

        // Вызов тестируемого метода
        String clientId = "123456789";
        String result = yclientsService.getStaffs(clientId);

        // Проверка результатов
        assertEquals("success response", result);
    }

    @Test
    public void testGetBookServices() throws IOException {
        // Подготовка мокового ответа от API
        Call<String> mockCall = mock(Call.class);
        Response<String> mockResponse = Response.success("success response");
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockYclientsApi.getBookServices(anyInt(), anyString(), anyString(), anyString())).thenReturn(mockCall);

        // Вызов тестируемого метода
        Integer companyId = 123;
        String partnerToken = "dummy-token";
        String result = yclientsService.getBookServices(companyId, partnerToken);

        // Проверка результатов
        assertEquals("success response", result);
    }

    // Добавьте другие тесты для других методов, если необходимо
}
