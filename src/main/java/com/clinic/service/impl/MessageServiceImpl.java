package com.clinic.service.impl;
import com.clinic.entity.Message;
import com.clinic.repository.MessageRepository;
import com.clinic.service.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    /* ---------- helper ---------- */
    private void save(Message msg) {
        msg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(msg);
    }

    /* ---------- admin ---------- */
    @Override
    public void sendMessageToAdmin(Message message) {
        message.setRecipientType(Message.RecipientType.ADMIN);
        save(message);
    }

    /* ---------- doctor ---------- */
    @Override
    public void sendMessageToDoctor(Message message) {
        message.setRecipientType(Message.RecipientType.DOCTOR);
        save(message);
    }

    /* ---------- patient ---------- */
    @Override
    public void sendMessageToPatient(Message message) {
        message.setRecipientType(Message.RecipientType.PATIENT);
        save(message);
    }
}