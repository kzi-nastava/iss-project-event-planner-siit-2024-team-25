package com.team25.event.planner.email.service;

import com.team25.event.planner.email.dto.EmailDTO;
import com.team25.event.planner.email.exception.EmailSendFailedException;

public interface EmailSenderService {
    void sendEmail(EmailDTO email) throws EmailSendFailedException;
}
