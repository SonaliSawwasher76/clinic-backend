package com.clinic.service.services;

import com.clinic.entity.Message;

public interface MessageService {
    void sendMessageToAdmin(Message message);
    void sendMessageToDoctor(Message message);
    void sendMessageToPatient(Message message);
}
