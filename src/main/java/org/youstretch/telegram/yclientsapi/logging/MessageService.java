package org.youstretch.telegram.yclientsapi.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;

// В вашем классе для обработки сообщений
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void processIncomingMessage(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setChatId(message.getChatId());
        entity.setMessageText(message.getText());
        entity.setTimestamp(LocalDateTime.now());
        messageRepository.save(entity);
    }
}
