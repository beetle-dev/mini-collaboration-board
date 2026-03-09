package com.minicollaborationboard.global.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
public class GmailEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String MAIL_SENDER;

    @Override
    public void sendHtmlMessage(String from, String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(MAIL_SENDER);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);


        javaMailSender.send(message);
    }
}
