package org.youstretch.telegram.yclientsapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${telegram.name}")
    String telegramName;
    @Value("${telegram.token}")
    String telegramToken;
    @Value("${yclients.partnerToken}")
    String yclientsPartnerToken;
    @Value("${yclients.companyId}")
    String yclientsCompanyId;
}
