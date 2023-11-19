package org.youstretch.telegram.yclientsapi.logging;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String messageText;
    private LocalDateTime timestamp;

    public void setChatId(Long chatId) {
        this.chatId=chatId;
    }

    public void setMessageText(String messageText) {
        this.messageText=messageText;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp=timestamp;
    }

    // геттеры и сеттеры
}

