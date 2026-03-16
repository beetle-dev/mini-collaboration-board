package com.minicollaborationboard.config;

import com.minicollaborationboard.common.service.EmailSender;
import com.minicollaborationboard.common.service.GmailEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
public class EmailSenderConfig {

    @Bean
    public EmailSender emailSender(JavaMailSender javaMailSender) {

        return new GmailEmailSender(javaMailSender);
    }
}
