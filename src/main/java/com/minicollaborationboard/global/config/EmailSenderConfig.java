package com.minicollaborationboard.global.config;

import com.minicollaborationboard.global.common.service.EmailSender;
import com.minicollaborationboard.global.common.service.GmailEmailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailSenderConfig {

    @Bean
    public EmailSender emailSender(GmailEmailSender gmailEmailSender) {

        return gmailEmailSender;
    }
}
