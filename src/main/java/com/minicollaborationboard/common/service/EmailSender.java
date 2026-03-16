package com.minicollaborationboard.common.service;

import jakarta.mail.MessagingException;

public interface EmailSender {

    void sendHtmlMessage(String from, String to, String subject, String htmlBody) throws MessagingException;
}
